package petrovskyi.replacer;

public class CommentReplacer implements Replacer {
    @Override
    public String replace(String original) {
        String pattern = "//.*|(?<replaceGroup>\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";

        return original.replaceAll(pattern, "${replaceGroup}");
    }
}
