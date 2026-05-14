package com.floweytech.agrotrack.reports.shared.interfaces.acl;

/**
 * Organization Context Facade Interface
 * @summary
 * Defines the contract for the Anti-Corruption Layer (ACL) of the Organization Context.
 * It serves as a gateway for other Bounded Contexts to validate data or request information
 * related to organizations and plots, ensuring loose coupling and domain isolation.
 *
 * @author FloweyTech developer team
 */
public interface OrganizationContextFacade {
    boolean existsPlotById(Long plotId);
}