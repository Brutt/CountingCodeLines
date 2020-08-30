package counter;

import java.io.InputStream;

public interface LineCounter {
    int count(InputStream inputStream);
}
