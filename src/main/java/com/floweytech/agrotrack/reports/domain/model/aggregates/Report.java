package com.floweytech.agrotrack.reports.domain.model.aggregates;

import com.floweytech.agrotrack.reports.domain.model.commands.CreateReportCommand;
import com.floweytech.agrotrack.reports.domain.model.events.ReportCreatedEvent;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.*;
import com.floweytech.agrotrack.reports.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.OrganizationId;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.ProfileId;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Report Aggregate Root
 * @summary
 * Represents a generated report for a specific plot and organization.
 * It acts as the root entity for the reporting context, holding the report status,
 * the defined period, and the calculated statistical metrics (snapshot) of the
 * requested environmental variable.
 *
 * @author FloweyTech developer team
 */
@Getter
@Entity
public class Report extends AuditableAbstractAggregateRoot<Report> {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column = @Column(
                    name="requested_by", nullable = true))})
    private ProfileId profileId;


    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Embedded
    @AttributeOverride(name = "value", column= @Column(name="plot_id", nullable = false))
    private PlotId plotId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name="organization_id", nullable = false))
    private OrganizationId organizationId;


    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Enumerated(EnumType.STRING)
    private MetricType metricType;

    @Embedded
    private ReportMetrics metrics;


    @Embedded
    private ReportPeriod reportPeriod;

    private LocalDateTime generatedAt;

    /**
     * Default constructor
     */
    protected Report() {}


    /**
     * Constructor for creating a new Report
     * @summary
     * Initializes a new Report instance with the provided command data and calculated metrics.
     * It automatically sets the status to GENERATED and records the generation timestamp.
     *
     * @param command The command containing report details (plot, organization, type, period).
     * @param metrics The calculated statistical metrics for the period.
     */
    public Report( CreateReportCommand command,ReportMetrics metrics) {
        this.profileId = command.profileId();
        this.status = ReportStatus.CREATED;
        this.plotId =command.plotId();
        this.organizationId = command.organizationId();
        this.type = command.type();
        this.metricType = command.metricType();
        this.reportPeriod = new ReportPeriod(command.periodStart(), command.periodEnd());
        this.generatedAt = LocalDateTime.now();
        this.metrics = metrics;

    }

    /**
     * Post-persist lifecycle hook
     * @summary
     * Executed automatically after the report is persisted to the database.
     * It registers a domain event (ReportCreatedEvent) carrying the new Report ID.
     */
    @PostPersist
    public void onPostPersist() {
        this.registerEvent(new ReportCreatedEvent(
                this,
                this.getId(),
                this.plotId,
                this.organizationId,
                this.type
        ));
    }

}
