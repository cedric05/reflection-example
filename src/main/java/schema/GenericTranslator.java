package schema;

import static schema.utils.getSample;
import static schema.utils.printDoc2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang.StringUtils;

import schema.pojo.Document;
import schema.pojo.Document2;

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

    public static void main(String args[]) throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        Document doc = new Document();
        doc.setId("1");
        doc.setName("Shiva Prasanth");

        Document2 newdoc = getDoc2BySchema(doc);
        printDoc2(newdoc);
    }

    private static Document2 getDoc2BySchema(Document doc)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        HashMap<Method, Method> methodMap = getMethodMap(DOC_DOC2_SCHEMA_JSON, Document.class, Document2.class);
        Document2 newdoc = new Document2();
        for (Entry<Method, Method> map : methodMap.entrySet()) {
            Method destMethod = map.getKey();
            Method sourceMethod = map.getValue();
            destMethod.invoke(newdoc, sourceMethod.invoke(doc));
        }
        return newdoc;
    }

    @SuppressWarnings( "rawtypes" )
    public static <Source, Dest> HashMap<Method, Method> getMethodMap(String schemaFilename, Class<Source> source,
            Class<Dest> dest) throws NoSuchMethodException {
        String schema = getSample(schemaFilename);
        JsonObject schemaJson = gson.fromJson(schema, JsonObject.class);
        HashMap<Method, Method> MethodMap = new HashMap<Method, Method>();
        for (Entry<String, JsonElement> e : schemaJson.entrySet()) {
            String field = e.getKey();
            JsonObject value = e.getValue().getAsJsonObject();

            String type = value.get("type").getAsString();
            Class typeClass = getClass(type);

            Method sourceMethod = getSourceMethod(source, field, value);

            Method destMethod = getDestMethod(dest, value, typeClass);

            MethodMap.put(destMethod, sourceMethod);
        }
        return MethodMap;
    }

    @SuppressWarnings( "deprecation" )
    private static <Dest> Method getDestMethod(Class<Dest> dest, JsonObject value,
            @SuppressWarnings("rawtypes") Class typeClass) throws NoSuchMethodException, SecurityException {
        JsonElement destJsonElement = value.get(DESTINATION);
        String destString;
        if (destJsonElement != null) {
            destString = "set" + StringUtils.capitalise(destJsonElement.getAsString());
        } else {
            JsonObject destMethodName = value.get(DESTINATION_METHOD).getAsJsonObject();
            destString = destMethodName.getAsString();
        }
        Method destMethod = dest.getMethod(destString, typeClass);
        return destMethod;
    }

    @SuppressWarnings( "deprecation" )
    private static <Source> Method getSourceMethod(Class<Source> source, String field, JsonObject value)
            throws NoSuchMethodException {
        JsonElement sourceJsonElement = value.get(SOURCE);
        String sourceString;
        if (sourceJsonElement == null) {
            sourceString = "get" + StringUtils.capitalise(field);
        } else {
            sourceString = "get" + sourceJsonElement.getAsString();
        }
        Method sourceMethod = source.getMethod(sourceString);
        return sourceMethod;
    }
    @SuppressWarnings("rawtypes")
    private static Class getClass(String type) {
        Class typeClass;
        switch (type) {
        case "int":
            typeClass = int.class;
            break;
        // case "int[]":
        // typeClass = int[].class;
        // break;
        case "Integer":
            typeClass = Integer.class;
            break;
        case "String":
            typeClass = String.class;
            break;
        default:
            typeClass = String.class;
        }
        return typeClass;
    }
}