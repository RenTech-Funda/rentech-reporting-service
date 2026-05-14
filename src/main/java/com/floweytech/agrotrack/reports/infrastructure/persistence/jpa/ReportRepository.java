package com.floweytech.agrotrack.reports.infrastructure.persistence.jpa;

import com.floweytech.agrotrack.reports.domain.model.aggregates.Report;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.ReportPeriod;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.ReportType;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.ProfileId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Report repository
 * <p>
 *   This interface is used to interact with the database and perform CRUD and business
 *   command and query supporting operations on the Report entity
 * </p>
 */
@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {

    boolean existsByPlotIdAndTypeAndReportPeriod(PlotId plotId, ReportType type, ReportPeriod reportPeriod);

    /**
     * Find all reports by Profile ID
     * @summary
     * Retrieves all reports associated with a specific profile ID.
     *
     * @param profileId The ProfileId value object.
     * @return A list of Report entities.
     */
    List<Report> findAllByProfileId(ProfileId profileId);
}
