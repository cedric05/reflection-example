package schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import schema.generictest_docs.ara1;
import schema.generictest_docs.ara2;
import schema.generictest_docs.doc1;
import schema.generictest_docs.doc2;

/**
 * Unit test for simple App.
 */
public class GenericTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);

    }

    @Test
    public void testGeneric() throws NoSuchMethodException, FileNotFoundException, IOException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        String schemaFilename = "/doct1-doct2-schema.json";
        String Schema = getSchemaByFile(schemaFilename);
        HashMap<Method, Method> methodMap = GenericTranslator.getMethodMap(Schema, doc1.class, doc2.class);
        assertEquals(3, methodMap.size());

        String name = "shiva prasanth";
        String description = "sample descripition";
        int id = 1;

        // creating new document
        doc1 d = new doc1();
        d.setName(name);
        d.setDescription(description);
        d.setId(id);

        doc2 d2 = new doc2();
        runMethodMap(methodMap, d, d2);
        assertEquals(d2.getRollno(), id);
        assertEquals(d2.getContent(), description);
        assertEquals(d2.getFull_name(), name);

        // String sample = utils.getSample(schemaFilename);
        // System.out.printf("sample: %s\n", sample);
        // GenericTranslator.getMethodMap(schemaFilename, doc1.class, doc2.class);

    }

    private <doc1, doc2> void runMethodMap(HashMap<Method, Method> methodMap, doc1 d, doc2 d2)
            throws IllegalAccessException, InvocationTargetException {
        Set<Entry<Method, Method>> entrySet = methodMap.entrySet();
        for (Entry<Method, Method> e : entrySet) {
            Method sMethod = e.getKey();
            Method gMethod = e.getValue();
            sMethod.invoke(d2, gMethod.invoke(d));
        }
    }

    private String getSchemaByFile(String schemaFilename) throws FileNotFoundException, IOException {
        File resource = new File(GenericTest.class.getResource(schemaFilename).getFile());
        String Schema = utils.getFileContents(resource.getAbsolutePath());
        return Schema;
    }

    @Test
    public void testwithAarray() throws FileNotFoundException, IOException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        String filename = "/ara1-ara2-schema.json";
        String schema = getSchemaByFile(filename);
        Class<ara1> source = ara1.class;
        Class<ara2> dest = ara2.class;
        HashMap<Method, Method> methodMap = GenericTranslator.getMethodMap(schema, source, dest);
        assertEquals(methodMap.size(),1);


        int a[] = {1,2,3};
        ara1 doc = new ara1();
        doc.setA(a);

        ara2 doc2 = new ara2();

        runMethodMap(methodMap, doc, doc2);

        int[] b = doc2.getB();
        assertEquals(b, a);
        

        // for()
        
    }
}
