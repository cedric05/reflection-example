package schema;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class GenericTranslator<Source, Dest> {
    /**
     * destination method name
     */

    private static final String DESTINATION_METHOD = "destinationMethod";
    /**
     * source field name
     */

    private static final String SOURCE = "source";
    /**
     * destination field name
     */

    private static final String DESTINATION = "destination";

    private static Gson gson = new Gson();
    private HashMap<SetterImpl<Dest>, GetterImpl<Source>> MethodMap;
    private Class<Source> source;
    private Class<Dest> dest;

    public GenericTranslator(String schema, Class<Source> source, Class<Dest> dest)
            throws NoSuchMethodException, SecurityException, NoSuchFieldException {
        this.source = source;
        this.dest = dest;
        JsonObject schemaJson = gson.fromJson(schema, JsonObject.class);
        MethodMap = new HashMap<SetterImpl<Dest>, GetterImpl<Source>>();
        for (Entry<String, JsonElement> e : schemaJson.entrySet()) {
            String field = e.getKey();
            JsonElement fieldValue = e.getValue();

            GetterImpl<Source> sourceMethod = getSourceMethod(field, fieldValue);

            Class<?> returnType = sourceMethod.ReturnType;

            SetterImpl<Dest> destMethod = getDestMethod(fieldValue, returnType);
            MethodMap.put(destMethod, sourceMethod);
        }
    }

    public void translate(Source source, Dest destObj)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, NoSuchFieldException, InstantiationException {
        Set<Entry<SetterImpl<Dest>, GetterImpl<Source>>> entrySet = MethodMap.entrySet();
        for (Entry<SetterImpl<Dest>, GetterImpl<Source>> entry : entrySet) {
            SetterImpl<Dest> destMethod = entry.getKey();

            GetterImpl<Source> sourceMethod = entry.getValue();
            Object value = sourceMethod.getReturnValue(source);
            destMethod.setValue(destObj, value);
            // SetterImpl sim = new SetterImpl<>();
            // sim.intialize(dest, "name");
            // try {
            //     sim.setValue(destObj, value);
            // } catch (NoSuchMethodException e) {
            //     e.printStackTrace();
            // } catch (SecurityException e) {
            //     e.printStackTrace();
            // } catch (IllegalAccessException e) {
            //     e.printStackTrace();
            // } catch (IllegalArgumentException e) {
            //     e.printStackTrace();
            // } catch (InvocationTargetException e) {
            //     e.printStackTrace();
            // } catch (NoSuchFieldException e) {
            //     e.printStackTrace();
            // } catch (InstantiationException e) {
            //     e.printStackTrace();
            // }
            // destMethod.invoke(destObj,value );
        }
    }

    private SetterImpl<Dest> getDestMethod(JsonElement destDescriptor, @SuppressWarnings("rawtypes") Class typeClass)
            throws NoSuchMethodException, SecurityException, NoSuchFieldException {
        String destMethodName;

        // if descriptor is string itself, then value will be field name
        if (!destDescriptor.isJsonObject()) {
            String valueString = destDescriptor.getAsString();
            destMethodName =  valueString;
        } else {
            // in else case descriptor should contain destination or destinantion method
            // name
            JsonObject descriptor = destDescriptor.getAsJsonObject();
            JsonElement destJsonElement = descriptor.get(DESTINATION);
            if (destJsonElement != null) {
                // destination is defined
                destMethodName = destJsonElement.getAsString();
            } else {
                // destiantion is not defined, going with destination method name
                destMethodName = descriptor.get(DESTINATION_METHOD).getAsString();
            }
        }
        SetterImpl<Dest> setterImpl = new SetterImpl<Dest>();
        setterImpl.intialize(dest, destMethodName);
        // Method destMethod = dest.getMethod(destMethodName, typeClass);
        return setterImpl;
    }

    @SuppressWarnings("deprecation")
    private GetterImpl<Source> getSourceMethod(String field, JsonElement value) throws NoSuchMethodException {
        String methodSuffix;
        if (value.isJsonObject()) {
            JsonElement sourceJsonElement = value.getAsJsonObject().get(SOURCE);
            {
                if (sourceJsonElement == null) {
                    methodSuffix = field;
                } else {
                    methodSuffix = sourceJsonElement.getAsString();
                }
            }
        } else {
            methodSuffix = field;
        }
        GetterImpl<Source> getterImpl = new GetterImpl<Source>();
        getterImpl.initialize(source, methodSuffix);
        // sourceMethodName = "get" + StringUtils.capitalise(methodSuffix);
        // Method sourceMethod = source.getMethod(sourceMethodName);
        return getterImpl;
    }

}