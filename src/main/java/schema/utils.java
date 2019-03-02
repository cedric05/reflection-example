package schema;

import java.util.Scanner;

import schema.pojo.Document2;

public class utils {
    public static String getSample(String sample_filename){
    Scanner scanner = new Scanner(App.class.getResourceAsStream(sample_filename), "UTF-8");
    String text = scanner.useDelimiter("\\A").next();
    scanner.close();
    return text;
    }
    public static void printDoc2(Document2 newdoc) {
        System.out.printf("new doc id2: %s\n", newdoc.getId2());
        System.out.printf("new doc name2: %s\n", newdoc.getName2());
    }
}