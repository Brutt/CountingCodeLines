package petrovskyi.service;

import lombok.RequiredArgsConstructor;
import petrovskyi.counter.LineCounter;
import petrovskyi.entity.FileDirectoryHierarchy;
import petrovskyi.entity.SourceFileReportStatistic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FileStatisticService {
    private final LineCounter lineCounter;

    public List<SourceFileReportStatistic> getStatistics(Path root, FileDirectoryHierarchy fileDirectoryHierarchy) throws IOException {
        boolean isDirectory = Files.isDirectory(root);
        List<SourceFileReportStatistic> statistics = new ArrayList<>();

        if (isDirectory) {
            int totalLinesNumber = getTotalCodeLinesAndFillStatistics(root,
                    fileDirectoryHierarchy.getFileDirectoryPathToFiles(), 1, statistics);

            SourceFileReportStatistic statistic = new SourceFileReportStatistic();
            statistic.setPath(root);
            statistic.setDepth(0);
            statistic.setDirectory(isDirectory);
            statistic.setLinesNumber(totalLinesNumber);

            statistics.add(statistic);

            Collections.reverse(statistics); //reverse list to get folders on the first places
        } else {
            getTotalCodeLinesAndFillStatistics(root, fileDirectoryHierarchy.getFileDirectoryPathToFiles(), 0, statistics);
        }

        return statistics;
    }

    int getTotalCodeLinesAndFillStatistics(Path startPath,
                                           Map<Path, List<Path>> fileDirectoryPathToFiles,
                                           int depth,
                                           List<SourceFileReportStatistic> statistics) throws IOException {
        List<Path> paths = fileDirectoryPathToFiles.get(startPath);
        if (paths == null) {
            return 0;
        }

        int totalLinesNumber = 0;
        int linesNumber;

        for (Path path : paths) {
            SourceFileReportStatistic statistic = new SourceFileReportStatistic();
            statistic.setPath(path);
            statistic.setDepth(depth);

            if (Files.isDirectory(path)) {
                statistic.setDirectory(true);
                linesNumber = getTotalCodeLinesAndFillStatistics(path, fileDirectoryPathToFiles, depth + 1, statistics);
            } else {
                try (InputStream inputStream = new FileInputStream(path.toString())) {
                    linesNumber = lineCounter.count(inputStream);
                }
                statistic.setDirectory(false);
            }

            statistic.setLinesNumber(linesNumber);
            totalLinesNumber += linesNumber;

            statistics.add(statistic);
        }

        return totalLinesNumber;
    }
}
