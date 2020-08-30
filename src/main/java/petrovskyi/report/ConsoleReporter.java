package petrovskyi.report;

import petrovskyi.entity.SourceFileReportStatistic;

import java.util.List;

public class ConsoleReporter implements Reporter {
    @Override
    public void write(List<SourceFileReportStatistic> statistics) {
        for (SourceFileReportStatistic statistic : statistics) {
            String stringStatistic = indent(statistic.getDepth()) +
                    statistic.getPath().getFileName().toString() +
                    " : " +
                    statistic.getLinesNumber();

            System.out.println(stringStatistic);
        }
    }

    private StringBuilder indent(int spaceCount) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < spaceCount; i++) {
            stringBuilder.append(" ");
        }

        return stringBuilder;
    }
}
