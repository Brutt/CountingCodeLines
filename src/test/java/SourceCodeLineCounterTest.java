import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceCodeLineCounterTest {


    @Test
    @DisplayName("Should return 3 lines of source code")
    void count3Lines() {
        String code = "// This file contains 3 lines of code\n" +
                "    public interface Dave {\n" +
                "        /**\n" +
                "         * count the number of lines in a file\n" +
                "         */\n" +
                "        int countLines(File inFile); // not the real signature!\n" +
                "    }";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(code.getBytes());

        LineCounter lineCounter = new SourceCodeLineCounter(byteArrayInputStream, new CommentReplacer());
        int sourceCodeLines = lineCounter.count();

        assertEquals(3, sourceCodeLines);
    }

    @Test
    @DisplayName("Should return 5 lines of source code")
    void count5Lines() {
        String code = "/*****\n" +
                "* This is a test program with 5 lines of code\n" +
                "*  \\/* no nesting allowed!\n" +
                "//*****/\n" +
                "/***/// Slightly pathological comment ending...\n" +
                "\n" +
                "public class Hello {\n" +
                "    public static final void main(String[] args) { // gotta love Java\n" +
                "        // Say hello\n" +
                "        System./*wait*/out./*for*/println/*it*/(\"Hello/*\");\n" +
                "    }\n" +
                "\n" +
                "}";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(code.getBytes());

        LineCounter lineCounter = new SourceCodeLineCounter(byteArrayInputStream, new CommentReplacer());
        int sourceCodeLines = lineCounter.count();

        assertEquals(5, sourceCodeLines);
    }
}


