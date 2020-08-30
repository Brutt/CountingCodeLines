package petrovskyi.service;

import petrovskyi.counter.LineCounter;
import petrovskyi.counter.SourceCodeLineCounter;
import petrovskyi.entity.FileDirectoryHierarchy;
import petrovskyi.entity.SourceFileReportStatistic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import petrovskyi.replacer.CommentReplacer;
import petrovskyi.replacer.Replacer;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class FileStatisticServiceTest {
    private final Replacer REPLACER = new CommentReplacer();
    private final LineCounter LINE_COUNTER = new SourceCodeLineCounter(REPLACER);

    @Test
    @DisplayName("Should return list of statistics for dir1 folder(including) in resource directory")
    void getStatisticsForFolder() throws FileNotFoundException {
        Map<Path, List<Path>> fileDirectoryPathToFiles = new TreeMap<>();

        Path dir1 = Paths.get("src", "test", "resources", "dir1").toAbsolutePath();

        List<Path> dir1SubFiles = new ArrayList<>();
        dir1SubFiles.add(Paths.get("src", "test", "resources", "dir1", "dir1.1").toAbsolutePath());
        dir1SubFiles.add(Paths.get("src", "test", "resources", "dir1", "dir1.2").toAbsolutePath());
        dir1SubFiles.add(Paths.get("src", "test", "resources", "dir1", "1.txt").toAbsolutePath());

        fileDirectoryPathToFiles.put(dir1, dir1SubFiles);
        FileDirectoryHierarchy fileDirectoryHierarchy = new FileDirectoryHierarchy();
        fileDirectoryHierarchy.setFileDirectoryPathToFiles(fileDirectoryPathToFiles);

        FileStatisticService fileStatisticService = new FileStatisticService(LINE_COUNTER);
        List<SourceFileReportStatistic> statistics = fileStatisticService.getStatistics(dir1, fileDirectoryHierarchy);
        assertEquals(4, statistics.size());

        List<SourceFileReportStatistic> expectedStatistics = getStatisticsForDir11(true);
        assertEquals(expectedStatistics.size(), statistics.size());
        for (SourceFileReportStatistic statistic : statistics) {
            expectedStatistics.remove(statistic);
        }
        assertEquals(0, expectedStatistics.size());
    }

    @Test
    @DisplayName("Should return total number of lines in dir1 folder in resource directory and fill list of statistics for subfiles")
    void getTotalCodeLinesAndFillStatisticsForFolder() throws FileNotFoundException {
        Map<Path, List<Path>> fileDirectoryPathToFiles = new TreeMap<>();

        Path dir1 = Paths.get("src", "test", "resources", "dir1").toAbsolutePath();

        List<Path> dir1SubFiles = new ArrayList<>();
        dir1SubFiles.add(Paths.get("src", "test", "resources", "dir1", "dir1.1").toAbsolutePath());
        dir1SubFiles.add(Paths.get("src", "test", "resources", "dir1", "dir1.2").toAbsolutePath());
        dir1SubFiles.add(Paths.get("src", "test", "resources", "dir1", "1.txt").toAbsolutePath());

        fileDirectoryPathToFiles.put(dir1, dir1SubFiles);

        FileStatisticService fileStatisticService = new FileStatisticService(LINE_COUNTER);
        int totalCodeLines = fileStatisticService.getTotalCodeLinesAndFillStatistics(dir1, fileDirectoryPathToFiles, 1);

        assertEquals(3, totalCodeLines); //file 1.txt contains 3 lines

        List<SourceFileReportStatistic> statistics = fileStatisticService.getStatistics();
        assertEquals(3, statistics.size());

        List<SourceFileReportStatistic> expectedStatistics = getStatisticsForDir11(false);
        assertEquals(expectedStatistics.size(), statistics.size());
        for (SourceFileReportStatistic statistic : statistics) {
            expectedStatistics.remove(statistic);
        }
        assertEquals(0, expectedStatistics.size());
    }

    @Test
    @DisplayName("Should return total number of lines for 1.txt file in resource directory and fill list of statistics for it")
    void getTotalCodeLinesAndFillStatisticsForFile() throws FileNotFoundException {
        Map<Path, List<Path>> fileDirectoryPathToFiles = new TreeMap<>();

        Path txt1File = Paths.get("src", "test", "resources", "dir1", "1.txt").toAbsolutePath();

        List<Path> txt1FileSubFiles = new ArrayList<>();
        txt1FileSubFiles.add(Paths.get("src", "test", "resources", "dir1", "1.txt").toAbsolutePath());

        fileDirectoryPathToFiles.put(txt1File, txt1FileSubFiles);

        FileStatisticService fileStatisticService = new FileStatisticService(LINE_COUNTER);
        int totalCodeLines = fileStatisticService.getTotalCodeLinesAndFillStatistics(txt1File, fileDirectoryPathToFiles, 0);

        assertEquals(3, totalCodeLines); //file 1.txt contains 3 lines

        List<SourceFileReportStatistic> statistics = fileStatisticService.getStatistics();
        assertEquals(1, statistics.size());

        List<SourceFileReportStatistic> expectedStatistics = getStatisticsFor1TxtFile();
        assertEquals(expectedStatistics, statistics);
    }

    private List<SourceFileReportStatistic> getStatisticsForDir11(boolean withRoot){
        List<SourceFileReportStatistic> expectedStatistics = new ArrayList<>();

        if(withRoot){
            SourceFileReportStatistic dir11Statistic = new SourceFileReportStatistic();
            dir11Statistic.setDirectory(true);
            dir11Statistic.setDepth(0);
            dir11Statistic.setLinesNumber(3);
            dir11Statistic.setPath(Paths.get("src", "test", "resources", "dir1").toAbsolutePath());
            expectedStatistics.add(dir11Statistic);
        }

        SourceFileReportStatistic dir11Statistic = new SourceFileReportStatistic();
        dir11Statistic.setDirectory(true);
        dir11Statistic.setDepth(1);
        dir11Statistic.setLinesNumber(0);
        dir11Statistic.setPath(Paths.get("src", "test", "resources", "dir1", "dir1.1").toAbsolutePath());
        expectedStatistics.add(dir11Statistic);

        SourceFileReportStatistic dir12Statistic = new SourceFileReportStatistic();
        dir12Statistic.setDirectory(true);
        dir12Statistic.setDepth(1);
        dir12Statistic.setLinesNumber(0);
        dir12Statistic.setPath(Paths.get("src", "test", "resources", "dir1", "dir1.2").toAbsolutePath());
        expectedStatistics.add(dir12Statistic);

        SourceFileReportStatistic txt1FileStatistic = new SourceFileReportStatistic();
        txt1FileStatistic.setDirectory(false);
        txt1FileStatistic.setDepth(1);
        txt1FileStatistic.setLinesNumber(3);
        txt1FileStatistic.setPath(Paths.get("src", "test", "resources", "dir1", "1.txt").toAbsolutePath());
        expectedStatistics.add(txt1FileStatistic);

        return expectedStatistics;
    }

    private List<SourceFileReportStatistic> getStatisticsFor1TxtFile(){
        List<SourceFileReportStatistic> expectedStatistics = new ArrayList<>();

        SourceFileReportStatistic txt1FileStatistic = new SourceFileReportStatistic();
        txt1FileStatistic.setDirectory(false);
        txt1FileStatistic.setDepth(0);
        txt1FileStatistic.setLinesNumber(3);
        txt1FileStatistic.setPath(Paths.get("src", "test", "resources", "dir1", "1.txt").toAbsolutePath());
        expectedStatistics.add(txt1FileStatistic);

        return expectedStatistics;
    }
}