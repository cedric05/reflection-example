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

import schema.pojo.Document;
import schema.pojo.Document2;

public class GenericTranslator {
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

    @SuppressWarnings("unused")
    public static <Source, Dest> HashMap<Method, Method> getMethodMap(String schemaFilename, Class<Source> source,
            Class<Dest> dest) throws NoSuchMethodException {
        String schema = getSample(schemaFilename);
        JsonObject schemaJson = gson.fromJson(schema, JsonObject.class);
        HashMap<Method, Method> MethodMap = new HashMap<Method, Method>();
        for (Entry<String, JsonElement> e : schemaJson.entrySet()) {
            JsonObject value = e.getValue().getAsJsonObject();

            String sourceString = value.get("source").getAsString();
            Method sourceMethod = source.getMethod(sourceString);

            String destString = value.get("destination").getAsString();
            Method destMethod = dest.getMethod(destString, String.class);

            MethodMap.put(destMethod, sourceMethod);
        }
        return MethodMap;
    }
}