package schema;

import org.junit.Test;

import schema.Complex.Bus;
import schema.Complex.Bus2;
import schema.Complex.Tyre;
import schema.generictest_docs.ara1;
import schema.generictest_docs.ara2;
import schema.generictest_docs.doc1;
import schema.generictest_docs.doc2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class GenericTest {
    public void testDoc1Doc2(String schemaFilename) throws NoSuchMethodException, IOException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {

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
    public void testDoc1Schema1() throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException, InstantiationException {
        testDoc1Doc2("/doct1-doct2-schema.json");
    }

    @Test
    public void testDoc1Doc2Schema2() throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException, InstantiationException {
        testDoc1Doc2("/doct1-doct2-schema2.json");
    }

    private String getSchemaByFile(String schemaFilename) throws IOException {
        File resource = new File(GenericTest.class.getResource(schemaFilename).getFile());
        String Schema = utils.getFileContents(resource.getAbsolutePath());
        return Schema;
    }

    public void testArr1Arr2(String filename) throws IOException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, NoSuchFieldException, InstantiationException {
        String schema = getSchemaByFile(filename);
        GenericTranslator<ara1, ara2> translator = new GenericTranslator<ara1, ara2>(schema, ara1.class, ara2.class);

        int[] a = {1, 2, 3};
        ara1 doc = new ara1();
        doc.setA(a);

        ara2 doc2 = new ara2();

        translator.translate(doc, doc2);

        int[] b = doc2.getB();
        assertEquals(b, a);

    }

    @Test
    public void testWithMoregeneric() throws IOException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, NoSuchFieldException, InstantiationException {
        String filename = "/ara1-ara2-schema-version2.json";
        testArr1Arr2(filename);
    }

    @Test
    public void testwithmoreSpecific() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, IOException, NoSuchFieldException, InstantiationException {
        String filename = "/ara1-ara2-schema.json";
        testArr1Arr2(filename);
    }

    @Test
    public void testwithdestMethodDefined() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException, NoSuchFieldException, InstantiationException {
        String filename = "/ara1-ara2-schemav1.json";
        testArr1Arr2(filename);
    }

    @Test
    public void testBus1Bus2() throws IOException, NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchFieldException, InstantiationException {
        String filename = "/bus-bus2-complex-schema.json";
        String schema = getSchemaByFile(filename);
        GenericTranslator translator = new GenericTranslator<Bus, Bus2>(schema, Bus.class, Bus2.class);
        
        Tyre tyre = new Tyre();
        String name = "tyrename";

        tyre.setName(name);
        String seatname = "finolex";
        
        Bus bus = new Bus();
        bus.setTyre(tyre);
        bus.setSeatname(seatname);
        // Bus.class.getClass().getDeclaredMethod(name);

        Bus2 modifiedBus = new Bus2();

        translator.translate(bus, modifiedBus);
        assertEquals(name, modifiedBus.getName());
        assertEquals(seatname, modifiedBus.getSeat().getName());

    }
}
