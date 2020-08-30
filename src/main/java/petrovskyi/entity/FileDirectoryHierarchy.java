package petrovskyi.entity;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FileDirectoryHierarchy {
    private Map<Path, List<Path>> fileDirectoryPathToFiles;
}
