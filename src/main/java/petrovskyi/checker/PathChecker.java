package petrovskyi.checker;

import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class PathChecker implements Checker {
    private final Path filenamePath;
    private final String javaExtension;

    @Override
    public boolean check() {
        return checkPath();
    }

    private boolean checkPath() {
        if (!Files.exists(filenamePath)) {
            System.out.println("Cannot find file or directory for the path <" + filenamePath + ">. " +
                    "Please, check the path you provided.");

            return false;
        }

        if (Files.isRegularFile(filenamePath) && !filenamePath.toString().endsWith(javaExtension)) {
            System.out.println("The file <" + filenamePath + "> is not java file. " +
                    "Please, provide a file with java extension.");

            return false;
        }

        return true;
    }


}
