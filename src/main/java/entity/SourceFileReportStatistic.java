package entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;

@Data
public class SourceFileReportStatistic {
    private boolean isDirectory;
    private int linesNumber;
    private Path path;
    private int depth;
}
