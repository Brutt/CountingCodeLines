package petrovskyi;

import petrovskyi.checker.PathChecker;
import petrovskyi.counter.LineCounter;
import petrovskyi.counter.SourceCodeLineCounter;
import petrovskyi.entity.SourceFileReportStatistic;
import petrovskyi.replacer.CommentReplacer;
import petrovskyi.replacer.Replacer;
import petrovskyi.report.ConsoleReporter;
import petrovskyi.report.Reporter;
import petrovskyi.service.FileDirectoryService;
import petrovskyi.service.FileStatisticService;
import petrovskyi.worker.CountJavaSourceCodeLinesWorker;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Application {
    private static final String JAVA_EXTENSION = ".java";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please, provide file or folder path to process.");
            return;
        }

        Path filenamePath = Paths.get(args[0]).toAbsolutePath();

        if (!new PathChecker(filenamePath, ".java").check()) {
            System.out.println("The specified path failed validation");
            return;
        }

        Replacer replacer = new CommentReplacer();
        LineCounter lineCounter = new SourceCodeLineCounter(replacer);
        FileStatisticService fileStatisticService = new FileStatisticService(lineCounter);

        FileDirectoryService fileDirectoryService = new FileDirectoryService(JAVA_EXTENSION);

        CountJavaSourceCodeLinesWorker countJavaSourceCodeLinesWorker =
                new CountJavaSourceCodeLinesWorker(fileDirectoryService, fileStatisticService);
        List<SourceFileReportStatistic> statistics = countJavaSourceCodeLinesWorker.getStatistics(filenamePath);

        Reporter reporter = new ConsoleReporter();
        reporter.write(statistics);
    }

}