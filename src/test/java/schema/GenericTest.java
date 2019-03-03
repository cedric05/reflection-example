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
        File resource = new File(GenericTest.class.getResource(schemaFilename).getFile());
        String Schema = utils.getFileContents(resource.getAbsolutePath());
        HashMap<Method, Method> methodMap = GenericTranslator.getMethodMap(Schema, doc1.class, doc2.class);
        assertEquals(3, methodMap.size());
        
        String name = "shiva prasanth";
        String description = "sample descripition";
        int id = 1;
        
        doc1 d = new doc1();
        d.setName(name);
        d.setDescription(description);
        d.setId(id);
        doc2 d2 = new doc2();
        Set<Entry<Method, Method>> entrySet = methodMap.entrySet();
        for(Entry<Method, Method> e:entrySet){
            Method sMethod = e.getKey();
            Method gMethod = e.getValue();
            sMethod.invoke(d2,gMethod.invoke(d));
        }
        assertEquals(d2.getRollno(), id);
        assertEquals(d2.getContent(), description);
        assertEquals(d2.getFull_name(), name);

        // String sample = utils.getSample(schemaFilename);
        // System.out.printf("sample: %s\n", sample);
        // GenericTranslator.getMethodMap(schemaFilename, doc1.class, doc2.class);

    }
}
