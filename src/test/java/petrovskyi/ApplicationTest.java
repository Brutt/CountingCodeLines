package petrovskyi;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

class ApplicationTest {

    @Test
    void main() {
        String[] path = new String[]{
                Paths.get("src", "test", "resources").toAbsolutePath().toString()
        };

        Application.main(path);
    }
}