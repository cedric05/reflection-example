package schema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;



public class utils {
    public static String getSample(String resourceFileName) throws IOException {
        String absoluteFileName = getResouceFullFileName(resourceFileName);
        return getFileContents(absoluteFileName);
    }

    public static String getFileContents(String absoluteFileName) throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader(absoluteFileName);
        return IOUtils.toString(fileReader);
    }

    public static String getResouceFullFileName(String sample) {
        File file = new File(App.class.getResource(sample).getFile());
        return file.getAbsolutePath();
    }

}