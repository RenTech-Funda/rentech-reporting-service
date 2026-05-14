package com.floweytech.agrotrack.reports.domain.model.valueobjects;


import jakarta.persistence.Embeddable;

/**
 * Report ID Value Object
 * @summary
 * Represents the unique identifier for a Report within the system.
 * This Value Object encapsulates the identity of the aggregate root, ensuring type safety
 * and preventing the misuse of raw primitive types for domain identities.
 *
 * @author FloweyTech developer team
 */
@Embeddable
public record ReportId() {
}
