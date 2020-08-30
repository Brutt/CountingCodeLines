package service;

import entity.FileDirectoryHierarchy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileDirectoryServiceTest {

    @Test
    @DisplayName("Should get the hierarchy for the directory including parent folder of the provided directory")
    void getFileDirectoryHierarchyForDirectory() throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources", "dir1").toAbsolutePath();

        FileDirectoryService fileDirectoryService = new FileDirectoryService(".txt");
        FileDirectoryHierarchy fileDirectoryHierarchy = fileDirectoryService.getFileDirectoryHierarchy(resourceDirectory);

        Map<Path, List<Path>> fileDirectoryPathToFiles = fileDirectoryHierarchy.getFileDirectoryPathToFiles();
        assertEquals(3, fileDirectoryPathToFiles.size()); // dir1 + dir1.1 + resources

        List<Path> expectedPaths = new ArrayList<>();
        expectedPaths.add(Paths.get("src", "test", "resources").toAbsolutePath());
        expectedPaths.add(Paths.get("src", "test", "resources", "dir1").toAbsolutePath());
        expectedPaths.add(Paths.get("src", "test", "resources", "dir1", "dir1.1").toAbsolutePath());
        for (Path path : fileDirectoryPathToFiles.keySet()) {
            expectedPaths.remove(path);
        }
        assertEquals(0, expectedPaths.size());

        List<Path> paths = fileDirectoryPathToFiles.get(resourceDirectory);
        assertEquals(3, paths.size()); // dir1.1 + dir1.2 + 1.txt

        List<Path> expectedPathsInResourseDir = new ArrayList<>();
        expectedPathsInResourseDir.add(Paths.get("src", "test", "resources", "dir1", "dir1.1").toAbsolutePath());
        expectedPathsInResourseDir.add(Paths.get("src", "test", "resources", "dir1", "dir1.2").toAbsolutePath());
        expectedPathsInResourseDir.add(Paths.get("src", "test", "resources", "dir1", "1.txt").toAbsolutePath());
        for (Path path : fileDirectoryPathToFiles.get(resourceDirectory)) {
            expectedPathsInResourseDir.remove(path);
        }
        assertEquals(0, expectedPathsInResourseDir.size());
    }

    @Test
    @DisplayName("Should get the hierarchy for the file")
    void getFileDirectoryHierarchyForFile() throws IOException {
        Path resourceFile = Paths.get("src", "test", "resources", "dir1", "1.txt").toAbsolutePath();

        FileDirectoryService fileDirectoryService = new FileDirectoryService(".txt");
        FileDirectoryHierarchy fileDirectoryHierarchy = fileDirectoryService.getFileDirectoryHierarchy(resourceFile);

        Map<Path, List<Path>> fileDirectoryPathToFiles = fileDirectoryHierarchy.getFileDirectoryPathToFiles();
        assertEquals(1, fileDirectoryPathToFiles.size()); // 1.txt
        assertTrue(fileDirectoryPathToFiles.containsKey(resourceFile));

        List<Path> paths = fileDirectoryPathToFiles.get(resourceFile);
        assertEquals(1, paths.size()); // 1.txt

        assertEquals(resourceFile, paths.get(0));
    }

    @Test
    @DisplayName("Should get the hierarchy map for the directory including parent folder of the provided directory")
    void getDirectoryPathToFiles() throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources", "dir1").toAbsolutePath();

        FileDirectoryService fileDirectoryService = new FileDirectoryService(".txt");

        Map<Path, List<Path>> fileDirectoryPathToFiles = fileDirectoryService.getDirectoryPathToFiles(resourceDirectory);
        assertEquals(3, fileDirectoryPathToFiles.size()); // dir1 + dir1.1 + resources

        List<Path> expectedPaths = new ArrayList<>();
        expectedPaths.add(Paths.get("src", "test", "resources").toAbsolutePath());
        expectedPaths.add(Paths.get("src", "test", "resources", "dir1").toAbsolutePath());
        expectedPaths.add(Paths.get("src", "test", "resources", "dir1", "dir1.1").toAbsolutePath());
        for (Path path : fileDirectoryPathToFiles.keySet()) {
            expectedPaths.remove(path);
        }
        assertEquals(0, expectedPaths.size());

        List<Path> paths = fileDirectoryPathToFiles.get(resourceDirectory);
        assertEquals(3, paths.size()); // dir1.1 + dir1.2 + 1.txt

        List<Path> expectedPathsInResourseDir = new ArrayList<>();
        expectedPathsInResourseDir.add(Paths.get("src", "test", "resources", "dir1", "dir1.1").toAbsolutePath());
        expectedPathsInResourseDir.add(Paths.get("src", "test", "resources", "dir1", "dir1.2").toAbsolutePath());
        expectedPathsInResourseDir.add(Paths.get("src", "test", "resources", "dir1", "1.txt").toAbsolutePath());
        for (Path path : fileDirectoryPathToFiles.get(resourceDirectory)) {
            expectedPathsInResourseDir.remove(path);
        }
        assertEquals(0, expectedPathsInResourseDir.size());
    }

    @Test
    @DisplayName("Should get the hierarchy map for the file")
    void getFilePathToFile() throws IOException {
        Path resourceFile = Paths.get("src", "test", "resources", "dir1", "1.txt").toAbsolutePath();

        FileDirectoryService fileDirectoryService = new FileDirectoryService(".txt");

        Map<Path, List<Path>> filePathToFile = fileDirectoryService.getFilePathToFile(resourceFile);
        assertEquals(1, filePathToFile.size()); // 1.txt
        assertTrue(filePathToFile.containsKey(resourceFile));

        List<Path> paths = filePathToFile.get(resourceFile);
        assertEquals(1, paths.size()); // 1.txt

        assertEquals(resourceFile, paths.get(0));
    }
}