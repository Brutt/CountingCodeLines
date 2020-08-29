import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentReplacerTest {

    @Test
    @DisplayName("Should replace all comments with void")
    void replace() {
        String code = "// This file contains 3 lines of code\n" +
                "    public interface Dave {\n" +
                "        /**\n" +
                "         * count the number of lines in a file\n" +
                "         */\n" +
                "        int countLines(File inFile); // not the real signature!\n" +
                "    }";
        Replacer replacer = new CommentReplacer();
        String codeWoComments = replacer.replace(code);

        String expectedCodeWoComments = "public interface Dave {\n" +
                "    int countLines(File inFile);\n" +
                "}";

        assertEquals(expectedCodeWoComments, codeWoComments);
    }
}

