import java.io.InputStream;

public class SourceCodeLineCounter implements LineCounter {
    private final InputStream inputStream;

    public SourceCodeLineCounter(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int count() {
        return 0;
    }
}
