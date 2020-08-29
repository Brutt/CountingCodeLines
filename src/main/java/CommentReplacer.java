public class CommentReplacer implements Replacer {
    private final String pattern = "//.*|(?s)/\\*.*?\\*/|(?<replaceGroup>\"(?:\\\\[^\"]|\\\\\"|.)*?\")";

    @Override
    public String replace(String original) {
        return original.replaceAll(pattern, "${replaceGroup}");
    }
}
