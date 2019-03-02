package schema;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import schema.pojo.Document;

/**
 * Hello world!
 *
 */
public class App {
    /**
     *
     */

    private static final String sample_filename = "/sample.json";
    private static final String sample_filenamelist = "/samplelist.json";
    private static Gson gson = new Gson();

    public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        documentExample();
    }

    private static void documentExample()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String text = getSample(sample_filename);
        JsonObject Sample = gson.fromJson(text, JsonObject.class);
        Document doc = getNewDocument(Sample.getAsJsonObject());
        printExtracted(doc);
    }

    private static void documentListExample()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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

    private static String getSample(String sample_filename) {
        Scanner scanner = new Scanner(App.class.getResourceAsStream(sample_filename), "UTF-8");
        String text = scanner.useDelimiter("\\A").next();
        scanner.close();
        return text;
    }
}
