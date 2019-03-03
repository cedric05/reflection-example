package schema;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang.StringUtils;

public class GenericTranslator {
    /**
     *
     */

    private static final String DESTINATION_METHOD = "destinationMethod";
    /**
     *
     */

    private static final String SOURCE = "source";
    /**
     *
     */

    private static final String DESTINATION = "destination";
    /**
     *
     */

    private static final String DOC_DOC2_SCHEMA_JSON = "/doc-doc2-schema.json";
    private static Gson gson = new Gson();

    public GenericTranslator() {

    }

    public static <Source, Dest> HashMap<Method, Method> getMethodMap(String schema, Class<Source> source,
            Class<Dest> dest) throws NoSuchMethodException, IOException {
        JsonObject schemaJson = gson.fromJson(schema, JsonObject.class);
        HashMap<Method, Method> MethodMap = new HashMap<Method, Method>();
        for (Entry<String, JsonElement> e : schemaJson.entrySet()) {
            String field = e.getKey();
            JsonObject fieldValueJsonObject = e.getValue().getAsJsonObject();

            Method sourceMethod = getSourceMethod(source, field, fieldValueJsonObject);

            Class<?> returnType = sourceMethod.getReturnType();

            Method destMethod = getDestMethod(dest, fieldValueJsonObject, returnType);

            MethodMap.put(destMethod, sourceMethod);
        }
        return MethodMap;
    }

    @SuppressWarnings("deprecation")
    private static <Dest> Method getDestMethod(Class<Dest> dest, JsonObject destDescriptor,
            @SuppressWarnings("rawtypes") Class typeClass) throws NoSuchMethodException, SecurityException {
        String destMethodName;

        // if descriptor is string itself, then value will be field name
        if (!destDescriptor.isJsonObject()) {
            String valueString = destDescriptor.getAsString();
            destMethodName = "set" + StringUtils.capitalise(valueString);
        } else {
            // in else case descriptor should contain destination or destinantion method
            // name
            JsonElement destJsonElement = destDescriptor.get(DESTINATION);
            if (destJsonElement != null) {
                // destination is defined
                destMethodName = "set" + StringUtils.capitalise(destJsonElement.getAsString());
            } else {
                // destiantion is not defined, going with destination method name
                destMethodName = destDescriptor.get(DESTINATION_METHOD).getAsJsonObject().getAsString();
            }
        }
        Method destMethod = dest.getMethod(destMethodName, typeClass);
        return destMethod;
    }

    @SuppressWarnings("deprecation")
    private static <Source> Method getSourceMethod(Class<Source> source, String field, JsonObject value)
            throws NoSuchMethodException {
        JsonElement sourceJsonElement = value.get(SOURCE);
        String sourceMethodName;
        {
            String Methodsuffix;
            if (sourceJsonElement == null) {
                Methodsuffix = field;
            } else {
                Methodsuffix = sourceJsonElement.getAsString();
            }
            sourceMethodName = "get" + StringUtils.capitalise(Methodsuffix);
        }
        Method sourceMethod = source.getMethod(sourceMethodName);
        return sourceMethod;
    }
}