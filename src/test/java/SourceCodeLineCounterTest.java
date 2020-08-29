import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceCodeLineCounterTest {


    @Test
    @DisplayName("Should return 3 lines of source code")
    void count() {
        String code = "// This file contains 3 lines of code\n" +
                "    public interface Dave {\n" +
                "        /**\n" +
                "         * count the number of lines in a file\n" +
                "         */\n" +
                "        int countLines(File inFile); // not the real signature!\n" +
                "    }";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(code.getBytes());

        LineCounter lineCounter = new SourceCodeLineCounter(byteArrayInputStream);
        int sourceCodeLines = lineCounter.count();

        assertEquals(3, sourceCodeLines);
    }
}