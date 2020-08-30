package service;

import entity.FileDirectoryHierarchy;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class FileDirectoryService {
    private final String extension;

    public FileDirectoryHierarchy getFileDirectoryHierarchy(Path filenamePath) throws IOException {

        FileDirectoryHierarchy fileDirectoryHierarchy = new FileDirectoryHierarchy();

        if(Files.isDirectory(filenamePath)){
            fileDirectoryHierarchy.setFileDirectoryPathToFiles(getDirectoryPathToFiles(filenamePath));
        }else{
            fileDirectoryHierarchy.setFileDirectoryPathToFiles(getFilePathToFile(filenamePath));
        }

        return fileDirectoryHierarchy;
    }

    Map<Path, List<Path>> getDirectoryPathToFiles(Path filenamePath) throws IOException {
        Map<Path, List<Path>> directoryPathToFiles;
        try (Stream<Path> walk = Files.walk(filenamePath)) {
            directoryPathToFiles = walk
                    .filter(x -> x.toString().endsWith(extension) || Files.isDirectory(x))
                    .collect(Collectors.groupingBy(Path::getParent, TreeMap::new, Collectors.toList()));
        }

        return directoryPathToFiles;
    }

    Map<Path, List<Path>> getFilePathToFile(Path filenamePath) throws IOException {
        Map<Path, List<Path>> directoryPathToFiles;
        try (Stream<Path> walk = Files.walk(filenamePath)) {
            directoryPathToFiles = walk
                    .filter(x -> x.toString().endsWith(extension))
                    .collect(Collectors.groupingBy(Path::toAbsolutePath, TreeMap::new, Collectors.toList()));
        }

        return directoryPathToFiles;
    }
}
