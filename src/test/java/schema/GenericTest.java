package schema;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import schema.generictest_docs.ara1;
import schema.generictest_docs.ara2;
import schema.generictest_docs.doc1;
import schema.generictest_docs.doc2;

/**
 * Unit test for simple App.
 */
public class GenericTest {
    public void testDoc1Doc2(String schemaFilename) throws NoSuchMethodException, FileNotFoundException, IOException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String Schema = getSchemaByFile(schemaFilename);
        GenericTranslator<doc1, doc2> translator = new GenericTranslator<>(Schema, doc1.class, doc2.class);
        String name = "shiva prasanth";
        String description = "sample descripition";
        int id = 1;

        // creating new document
        doc1 d = new doc1();
        d.setName(name);
        d.setDescription(description);
        d.setId(id);

        doc2 d2 = new doc2();
        translator.translate(d, d2);
        assertEquals(d2.getRollno(), id);
        assertEquals(d2.getContent(), description);
        assertEquals(d2.getFull_name(), name);
    }

    @Test
    public void testDoc1Schema1() throws NoSuchMethodException, FileNotFoundException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException {
        testDoc1Doc2("/doct1-doct2-schema.json");
    }

    @Test
    public void testDoc1Doc2Schema2() throws NoSuchMethodException, FileNotFoundException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException {
        testDoc1Doc2("/doct1-doct2-schema2.json");
    }

    private String getSchemaByFile(String schemaFilename) throws FileNotFoundException, IOException {
        File resource = new File(GenericTest.class.getResource(schemaFilename).getFile());
        String Schema = utils.getFileContents(resource.getAbsolutePath());
        return Schema;
    }

    public void testArr1Arr2(String filename) throws FileNotFoundException, IOException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        String schema = getSchemaByFile(filename);
        GenericTranslator<ara1, ara2> translator = new GenericTranslator<ara1, ara2>(schema, ara1.class, ara2.class);

        int a[] = { 1, 2, 3 };
        ara1 doc = new ara1();
        doc.setA(a);

        ara2 doc2 = new ara2();

        translator.translate(doc, doc2);

        int[] b = doc2.getB();
        assertEquals(b, a);

    }

    @Test
    public void testWithMoregeneric() throws FileNotFoundException, IOException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        String filename = "/ara1-ara2-schema-version2.json";
        testArr1Arr2(filename);
    }

    @Test
    public void testwithmoreSpecific() throws FileNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, IOException {
        String filename = "/ara1-ara2-schema.json";
        testArr1Arr2(filename);
    }
}