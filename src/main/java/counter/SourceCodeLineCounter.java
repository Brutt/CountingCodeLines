package counter;

import replacer.Replacer;
import util.InputStreamToStringTransformer;

import java.io.InputStream;
import java.util.Arrays;

public class SourceCodeLineCounter implements LineCounter {
    private final String code;

    public SourceCodeLineCounter(InputStream inputStream, Replacer replacer) {
        String originCode = InputStreamToStringTransformer.transform(inputStream);
        this.code = replacer.replace(originCode);
    }

    @Override
    public int count() {
        return (int) Arrays.stream(code.split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .count();
    }
}
