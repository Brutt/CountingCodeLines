package petrovskyi.replacer;

public class CommentReplacer implements Replacer {
    @Override
    public String replace(String original) {
        String pattern = "//.*|(?s)/\\*.*?\\*/|(?<replaceGroup>\"(?:\\\\[^\"]|\\\\\"|.)*?\")";

        return original.replaceAll(pattern, "${replaceGroup}");
    }
}
