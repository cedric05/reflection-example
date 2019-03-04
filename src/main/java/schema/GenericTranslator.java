package schema;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private HashMap<Method, Method> MethodMap;
    private Class<Source> source;
    private Class<Dest> dest;

    public GenericTranslator(String schema, Class<Source> source, Class<Dest> dest)
            throws NoSuchMethodException {
        this.source = source;
        this.dest = dest;
        JsonObject schemaJson = gson.fromJson(schema, JsonObject.class);
        MethodMap = new HashMap<Method, Method>();
        for (Entry<String, JsonElement> e : schemaJson.entrySet()) {
            String field = e.getKey();
            JsonElement fieldValue = e.getValue();

            Method sourceMethod = getSourceMethod(field, fieldValue);

            Class<?> returnType = sourceMethod.getReturnType();

            Method destMethod = getDestMethod(fieldValue, returnType);

            MethodMap.put(destMethod, sourceMethod);
        }
    }

    public void translate(Source source, Dest dest)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Set<Entry<Method, Method>> entrySet = MethodMap.entrySet();
        for (Entry<Method, Method> entry : entrySet) {
            Method destMethod = entry.getKey();
            Method sourceMethod = entry.getValue();
            destMethod.invoke(dest, sourceMethod.invoke(source));
        }
    }

    @SuppressWarnings("deprecation")
    private Method getDestMethod(JsonElement destDescriptor, @SuppressWarnings("rawtypes") Class typeClass)
            throws NoSuchMethodException, SecurityException {
        String destMethodName;

        // if descriptor is string itself, then value will be field name
        if (!destDescriptor.isJsonObject()) {
            String valueString = destDescriptor.getAsString();
            destMethodName = "set" + StringUtils.capitalise(valueString);
        } else {
            // in else case descriptor should contain destination or destinantion method
            // name
            JsonObject descriptor = destDescriptor.getAsJsonObject();
            JsonElement destJsonElement = descriptor.get(DESTINATION);
            if (destJsonElement != null) {
                // destination is defined
                destMethodName = "set" + StringUtils.capitalise(destJsonElement.getAsString());
            } else {
                // destiantion is not defined, going with destination method name
                destMethodName = descriptor.get(DESTINATION_METHOD).getAsString();
            }
        }
        Method destMethod = dest.getMethod(destMethodName, typeClass);
        return destMethod;
    }

    @SuppressWarnings("deprecation")
    private Method getSourceMethod(String field, JsonElement value) throws NoSuchMethodException {
        String sourceMethodName;
        String Methodsuffix;
        if (value.isJsonObject()) {
            JsonElement sourceJsonElement = value.getAsJsonObject().get(SOURCE);
            {
                if (sourceJsonElement == null) {
                    Methodsuffix = field;
                } else {
                    Methodsuffix = sourceJsonElement.getAsString();
                }
            }
        } else {
            Methodsuffix = field;
        }
        sourceMethodName = "get" + StringUtils.capitalise(Methodsuffix);
        Method sourceMethod = source.getMethod(sourceMethodName);
        return sourceMethod;
    }
}