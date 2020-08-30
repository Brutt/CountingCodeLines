package petrovskyi.report;

import petrovskyi.entity.SourceFileReportStatistic;

import java.util.List;

public class ConsoleReporter implements Reporter {
    @Override
    public void write(List<SourceFileReportStatistic> statistics) {
        for (SourceFileReportStatistic statistic : statistics) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append(indent(statistic.getDepth()))
                    .append(statistic.getPath().getFileName().toString())
                    .append(" : ")
                    .append(statistic.getLinesNumber());

            System.out.println(stringBuilder);
        }
    }

    private String indent(int spaceCount) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < spaceCount; i++) {
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }
}
