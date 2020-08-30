package petrovskyi;

import petrovskyi.counter.LineCounter;
import petrovskyi.counter.SourceCodeLineCounter;
import petrovskyi.entity.FileDirectoryHierarchy;
import petrovskyi.entity.SourceFileReportStatistic;
import petrovskyi.replacer.CommentReplacer;
import petrovskyi.replacer.Replacer;
import petrovskyi.report.ConsoleReporter;
import petrovskyi.report.Reporter;
import petrovskyi.service.FileDirectoryService;
import petrovskyi.service.FileStatisticService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Application {
    private static final String javaExtension = ".java";
    private static final FileDirectoryService FILE_DIRECTORY_HOLDER = new FileDirectoryService(javaExtension);
    private static final Replacer REPLACER = new CommentReplacer();
    private static final LineCounter LINE_COUNTER = new SourceCodeLineCounter(REPLACER);
    private static final FileStatisticService FILE_STATISTIC_SERVICE = new FileStatisticService(LINE_COUNTER);
    private static final Reporter REPORTER = new ConsoleReporter();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please, provide file or folder path to process.");
            return;
        }

        Path filenamePath = Paths.get(args[0]);

        if (!checkPath(filenamePath)) {
            return;
        }

        FileDirectoryHierarchy fileDirectoryHierarchy = getFileDirectoryHierarchy(filenamePath);

        List<SourceFileReportStatistic> statistics = getSourceFileReportStatistics(filenamePath, fileDirectoryHierarchy);

        REPORTER.write(statistics);
    }

    private static FileDirectoryHierarchy getFileDirectoryHierarchy(Path filenamePath) {
        FileDirectoryHierarchy fileDirectoryHierarchy;
        try {
            fileDirectoryHierarchy = FILE_DIRECTORY_HOLDER.getFileDirectoryHierarchy(filenamePath);
        } catch (IOException e) {
            throw new RuntimeException("Error while trying to get hierarchy for " + filenamePath, e);
        }
        return fileDirectoryHierarchy;
    }

    private static List<SourceFileReportStatistic> getSourceFileReportStatistics(Path filenamePath, FileDirectoryHierarchy fileDirectoryHierarchy) {
        List<SourceFileReportStatistic> statistics;
        try {
            statistics = FILE_STATISTIC_SERVICE.getStatistics(filenamePath, fileDirectoryHierarchy);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error while trying to get file(s) petrovskyi.report statistic for " + filenamePath, e);
        }
        return statistics;
    }

    private static boolean checkPath(Path filenamePath) {
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