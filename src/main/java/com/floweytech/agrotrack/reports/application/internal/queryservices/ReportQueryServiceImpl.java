package com.floweytech.agrotrack.reports.application.internal.queryservices;
import com.floweytech.agrotrack.reports.domain.model.aggregates.Report;
import com.floweytech.agrotrack.reports.domain.model.queries.GetAllReportsByProfileIdQuery;
import com.floweytech.agrotrack.reports.domain.model.queries.GetReportByIdQuery;
import com.floweytech.agrotrack.reports.domain.services.ReportQueryService;
import com.floweytech.agrotrack.reports.infrastructure.persistence.jpa.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ReportQueryServiceImpl implements ReportQueryService {

    private final ReportRepository reportRepository;

    /**
     * Constructor
     * @param reportRepository the report repository.
     * @see ReportRepository
     */

    public ReportQueryServiceImpl(ReportRepository reportRepository){
        this.reportRepository = reportRepository;
    }

    //inherited javadoc
    @Override
    public Optional<Report> handle(GetReportByIdQuery query) {
        return reportRepository.findById(query.reportId());
    }

    @Override
    public List<Report> handle(GetAllReportsByProfileIdQuery query) {
        return reportRepository.findAllByProfileId(query.profileId());
    }
}
