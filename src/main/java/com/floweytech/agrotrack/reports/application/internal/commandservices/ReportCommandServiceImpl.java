package com.floweytech.agrotrack.reports.application.internal.commandservices;


import com.floweytech.agrotrack.reports.application.internal.outboundservices.acl.ExternalMonitoringService;
import com.floweytech.agrotrack.reports.application.internal.outboundservices.acl.ExternalOrganizationService;
import com.floweytech.agrotrack.reports.domain.model.aggregates.Report;
import com.floweytech.agrotrack.reports.domain.model.commands.CreateReportCommand;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.ReportMetrics;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.ReportPeriod;
import com.floweytech.agrotrack.reports.domain.services.ReportCommandService;
import com.floweytech.agrotrack.reports.infrastructure.persistence.jpa.ReportRepository;
import com.floweytech.agrotrack.reports.shared.interfaces.rest.resources.ReadingResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * Report Command Service Implementation
 * @summary
 * Implements the business logic for handling report-related commands.
 * It orchestrates the flow of validating external entities (Plots), fetching raw data
 * via ACLs, performing statistical calculations (Average, Min, Max), and persisting
 * the final Report aggregate.
 *
 * @author FloweyTech developer team
 */
@Service
public class ReportCommandServiceImpl implements ReportCommandService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportCommandServiceImpl.class);

    private final ReportRepository reportRepository;
    private final ExternalOrganizationService externalOrganizationService;
    private final ExternalMonitoringService externalMonitoringService;
    private final MessageSource messageSource;

    public ReportCommandServiceImpl(
            ReportRepository reportRepository,
            ExternalOrganizationService externalOrganizationService,
            ExternalMonitoringService externalMonitoringService,
            MessageSource messageSource) {
        this.reportRepository = reportRepository;
        this.externalOrganizationService = externalOrganizationService;
        this.externalMonitoringService = externalMonitoringService;
        this.messageSource = messageSource;
    }

    /**
     * Handle Create Report Command
     * @summary
     * Processes the command to create a new report. It validates the plot existence,
     * retrieves environmental readings, calculates metrics, avoids duplicates,
     * and saves the new report.
     *
     * @param command The CreateReportCommand containing request details.
     * @return The ID of the created report.
     * @throws IllegalArgumentException If the plot does not exist, a duplicate report exists, or saving fails.
     */
    @Override
    public Long handle(CreateReportCommand command ) {


        if (command.periodEnd().isBefore(command.periodStart())) {
            String errorMessage = messageSource.getMessage(
                    "report.period.invalid",
                    null,
                    LocaleContextHolder.getLocale()
            );
            throw new IllegalArgumentException(errorMessage);
        }

        var plotExists = externalOrganizationService.fetchPlotExists(command.plotId());
        if (!plotExists) {
            String errorMessage = messageSource.getMessage(
                    "report.plot.not.found",             // Clave
                    new Object[]{command.plotId().value()}, // Argumentos ({0})
                    LocaleContextHolder.getLocale()      // Idioma actual
            );
            throw new IllegalArgumentException(errorMessage);
        }

        var startDateTime = command.periodStart().atStartOfDay();
        var endDateTime = command.periodEnd().atTime(23, 59, 59);

        List<ReadingResource> readings = externalMonitoringService.fetchReadingsForReport(
                command.plotId().value(),
                command.metricType().name(),
                startDateTime,
                endDateTime
        );

        ReportMetrics metrics;
        if (readings.isEmpty()) {
            metrics = ReportMetrics.empty();
        } else {
            // Java Streams para calcular estadísticas rápidamente
            DoubleSummaryStatistics stats = readings.stream()
                    .mapToDouble(ReadingResource::value)
                    .summaryStatistics();

            metrics = new ReportMetrics(
                    stats.getAverage(),
                    stats.getMax(),
                    stats.getMin(),
                    (int) stats.getCount()
            );
        }

        var period = new ReportPeriod(command.periodStart(), command.periodEnd());

        var reportExists = reportRepository.existsByPlotIdAndTypeAndReportPeriod(
                command.plotId(),
                command.type(),
                period
        );

        if (reportExists) {
            String errorMessage = messageSource.getMessage(
                    "report.duplicate",
                    new Object[]{command.type()},
                    LocaleContextHolder.getLocale()
            );
            throw new IllegalArgumentException(errorMessage);
        }

        var report = new Report(command, metrics);
        try {
            reportRepository.save(report);
        } catch (Exception e) {
            LOGGER.error("Error saving report for plot {} organization {} metric {} period {} - {}",
                    command.plotId().value(),
                    command.organizationId().value(),
                    command.metricType(),
                    period,
                    e.getMessage(),
                    e);
            String errorMessage = messageSource.getMessage("report.save.error", null, LocaleContextHolder.getLocale());
            throw new IllegalArgumentException(errorMessage);
        }
        return report.getId();
    }

}
