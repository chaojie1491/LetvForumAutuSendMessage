package letv.Letv;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class PathTest {

    @Test
    public void testPath() throws IOException {
        File file = new File("");
        String filePath = file.getCanonicalPath();
        System.out.println(filePath);
    }
}
