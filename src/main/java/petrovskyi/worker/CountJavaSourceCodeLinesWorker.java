package petrovskyi.worker;

import lombok.RequiredArgsConstructor;
import petrovskyi.entity.FileDirectoryHierarchy;
import petrovskyi.entity.SourceFileReportStatistic;
import petrovskyi.service.FileDirectoryService;
import petrovskyi.service.FileStatisticService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class CountJavaSourceCodeLinesWorker {
    private final FileDirectoryService fileDirectoryService;
    private final FileStatisticService fileStatisticService;

    public List<SourceFileReportStatistic> getStatistics(Path filenamePath) {
        FileDirectoryHierarchy fileDirectoryHierarchy = getFileDirectoryHierarchy(filenamePath);

        return getSourceFileReportStatistics(filenamePath, fileDirectoryHierarchy);
    }

    private FileDirectoryHierarchy getFileDirectoryHierarchy(Path filenamePath) {
        FileDirectoryHierarchy fileDirectoryHierarchy;
        try {
            fileDirectoryHierarchy = fileDirectoryService.getFileDirectoryHierarchy(filenamePath);
        } catch (IOException e) {
            throw new RuntimeException("Error while trying to get hierarchy for " + filenamePath, e);
        }
        return fileDirectoryHierarchy;
    }

    private List<SourceFileReportStatistic> getSourceFileReportStatistics(Path filenamePath, FileDirectoryHierarchy fileDirectoryHierarchy) {
        List<SourceFileReportStatistic> statistics;
        try {
            statistics = fileStatisticService.getStatistics(filenamePath, fileDirectoryHierarchy);
        } catch (IOException e) {
            throw new RuntimeException("Error while trying to get file(s) petrovskyi.report statistic for " + filenamePath, e);
        }
        return statistics;
    }
}
