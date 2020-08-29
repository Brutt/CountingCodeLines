package replacer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentReplacerTest {

    @Test
    @DisplayName("Should replace trivial comments with void")
    void replaceTrivialComments() {
        String code = "// This file contains 3 lines of code\n" +
                "public interface Dave {\n" +
                "    /**\n" +
                "     * count the number of lines in a file\n" +
                "     */\n" +
                "    int countLines(File inFile); // not the real signature!\n" +
                "}";
        Replacer replacer = new CommentReplacer();
        String codeWoComments = replacer.replace(code);

        String expectedCodeWoComments = "\n" +
                "public interface Dave {\n" +
                "    \n" +
                "    int countLines(File inFile); \n" +
                "}";

        assertEquals(expectedCodeWoComments, codeWoComments);
    }

    @Test
    @DisplayName("Should replace all comments including inline ones with void")
    void replaceInlineComments() {
        String code = "/*****\n" +
                "* This is a test program with 5 lines of code\n" +
                "*  \\/* no nesting allowed!\n" +
                "//*****//***/// Slightly pathological comment ending...\n" +
                "\n" +
                "public class Hello {\n" +
                "    public static final void main(String[] args) { // gotta love Java\n" +
                "        // Say hello\n" +
                "        System./*wait*/out./*for*/println/*it*/(\"Hello/*\");\n" +
                "    }\n" +
                "\n" +
                "}";
        Replacer replacer = new CommentReplacer();
        String codeWoComments = replacer.replace(code);

        String expectedCodeWoComments = "\n" +
                "\n" +
                "public class Hello {\n" +
                "    public static final void main(String[] args) { \n" +
                "        \n" +
                "        System.out.println(\"Hello/*\");\n" +
                "    }\n" +
                "\n" +
                "}";

        assertEquals(expectedCodeWoComments, codeWoComments);
    }

    @Test
    @DisplayName("Replace comments in source with cyrillic letters")
    void replaceCommentsInSourceWithCyrillic() {
        String code = "// This file contains 3 lines of code\n" +
                "public interface Dave {\n" +
                "    /**\n" +
                "     * count the number of lines in a file\n" +
                "     */\n" +
                "    int countLines(File inFile); // not the real signature!\n" +
                "} Привіт";
        Replacer replacer = new CommentReplacer();
        String codeWoComments = replacer.replace(code);

        String expectedCodeWoComments = "\n" +
                "public interface Dave {\n" +
                "    \n" +
                "    int countLines(File inFile); \n" +
                "} Привіт";

        assertEquals(expectedCodeWoComments, codeWoComments);
    }
}

