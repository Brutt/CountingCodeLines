package petrovskyi.counter;

import petrovskyi.replacer.Replacer;
import petrovskyi.util.InputStreamToStringTransformer;

import java.io.InputStream;
import java.util.Arrays;

public class SourceCodeLineCounter implements LineCounter {
    private final String endOfLine = "\\r?\\n";
    private Replacer replacer;

    public SourceCodeLineCounter(Replacer replacer) {
        this.replacer = replacer;
    }

    @Override
    public int count(InputStream inputStream) {
        String code = useReplacer(inputStream);

        return (int) Arrays.stream(code.split(endOfLine))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .count();
    }

    private String useReplacer(InputStream inputStream) {
        String originCode = InputStreamToStringTransformer.transform(inputStream);

        return replacer.replace(originCode);
    }
}
