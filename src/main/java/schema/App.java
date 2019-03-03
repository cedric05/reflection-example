package schema;

import static schema.utils.getSample;
import static schema.utils.printDoc2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import schema.pojo.DocWithByteArray;
import schema.pojo.Document;
import schema.pojo.Document2;

/**
 * Hello world!
 *
 */
public class App {
    /**
     *
     */

    private static final String UNUSED = "unused";
    /**
     *
     */

    private static final String sample_filename = "/sample.json";
    private static final String sample_filenamelist = "/samplelist.json";
    private static final String arraydoc_filename = "/array.json";
    private static final String DOC_DOC2_SCHEMA_JSON = "/doc-doc2-schema.json";

    private static Gson gson = new Gson();

    public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException {
        Document doc = new Document();
        doc.setId("id2");
        doc.setName("name2");
        Document2 doc2 = getTranslatedDoc2(doc);
        printDoc2(doc2);

    }

    private static Document2 getTranslatedDoc2(Document doc)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        Field[] declaredFields = Document.class.getDeclaredFields();
        String schemaJsonString = getSample(DOC_DOC2_SCHEMA_JSON);
        JsonObject SchemaJson = gson.fromJson(schemaJsonString, JsonObject.class);
        Document2 doc2 = new Document2();
        for (Field field : declaredFields) {
            String name = field.getName();
            JsonElement jsonElement = SchemaJson.get(name);
            if (!jsonElement.isJsonNull()) {
                JsonObject FieldDescriptor = jsonElement.getAsJsonObject();
                String destinationFieldName = FieldDescriptor.get("destination").getAsString();
                String SourceMethodName = FieldDescriptor.get("source").getAsString();
                Method setMethod = Document2.class.getMethod(destinationFieldName, String.class);
                Method getMethod = Document.class.getMethod(SourceMethodName);
                setMethod.invoke(doc2, getMethod.invoke(doc));
            } else {
                System.out.printf("coudln't find translation %s", name);
            }
        }
        return doc2;
    }

    @SuppressWarnings(UNUSED)
    private static DocWithByteArray getDocArray()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        String sample = getSample(arraydoc_filename);
        JsonObject arrayjsonstring = gson.fromJson(sample, JsonObject.class);
        Method setId = DocWithByteArray.class.getMethod("setId", String[].class);
        DocWithByteArray doc = new DocWithByteArray();
        JsonArray asJsonArray = arrayjsonstring.get("id").getAsJsonArray();
        String[] array = new String[asJsonArray.size()];
        int count = 0;
        for (JsonElement ele : asJsonArray) {
            array[count] = ele.getAsString();
            count++;
        }
        setId.invoke(doc, new Object[] { array });
        return doc;
    }

    @SuppressWarnings(UNUSED)
    private static void docArrayPrint(DocWithByteArray doc) {
        String[] id = doc.getId();
        System.out.printf("doc array ids: %s %s", id[0], id[1]);
    }

    @SuppressWarnings(UNUSED)
    private static void documentExample()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        String text = getSample(sample_filename);
        JsonObject Sample = gson.fromJson(text, JsonObject.class);
        Document doc = getNewDocument(Sample.getAsJsonObject());
        printExtracted(doc);
    }

    @SuppressWarnings(UNUSED)
    private static void documentListExample()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        String text = getSample(sample_filenamelist);
        JsonArray Sample = gson.fromJson(text, JsonArray.class);
        for (JsonElement obj : Sample) {
            Document doc = getNewDocument(obj.getAsJsonObject());
            printExtracted(doc);
        }
    }

    private static void printExtracted(Document doc) {
        System.out.printf("doc id: %s\n", doc.getId());
        System.out.printf("doc name: %s\n", doc.getName());
    }

    private static Document getNewDocument(JsonObject Sample)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Set<Entry<String, JsonElement>> entrySet = Sample.entrySet();

        Method idMethod = Document.class.getMethod("setId", String.class);
        Method nameMethod = Document.class.getMethod("setName", String.class);

        // idMethod.invoke(doc, "hai");
        Document doc = new Document();
        for (Entry<String, JsonElement> obj : entrySet) {
            String key = obj.getKey();
            String value = obj.getValue().getAsString();
            switch (key) {
            case "id":
                idMethod.invoke(doc, value);
            case "name":
                nameMethod.invoke(doc, value);
            }
        }
        return doc;
    }

}
