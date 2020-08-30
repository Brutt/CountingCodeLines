package petrovskyi.report;

import petrovskyi.entity.SourceFileReportStatistic;

import java.util.List;

public interface Reporter {
    void write(List<SourceFileReportStatistic> statistics);
}
