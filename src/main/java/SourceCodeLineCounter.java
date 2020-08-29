import java.io.InputStream;

public class SourceCodeLineCounter implements LineCounter {
    private final String code;

    public SourceCodeLineCounter(InputStream inputStream, Replacer replacer) {
        String originCode = InputStreamToStringTransformer.transform(inputStream);
        this.code = replacer.replace(originCode);

    }

    @Override
    public int count() {
        System.out.println(code);

        return 0;
    }
}
