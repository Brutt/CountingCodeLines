package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class InputStreamToStringTransformer {

    public static String transform(InputStream inputStream) {
        return new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        )
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
