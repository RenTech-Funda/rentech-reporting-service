package com.floweytech.agrotrack.reports.application.internal.commandservices;

import com.floweytech.agrotrack.reports.application.internal.outboundservices.acl.ExternalMonitoringService;
import com.floweytech.agrotrack.reports.application.internal.outboundservices.acl.ExternalOrganizationService;
import com.floweytech.agrotrack.reports.domain.model.aggregates.Report;
import com.floweytech.agrotrack.reports.domain.model.commands.CreateReportCommand;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.MetricType;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.ReportPeriod;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.ReportType;
import com.floweytech.agrotrack.reports.infrastructure.persistence.jpa.ReportRepository;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.OrganizationId;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.ProfileId;
import com.floweytech.agrotrack.reports.shared.interfaces.rest.resources.ReadingResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportCommandServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ExternalOrganizationService externalOrganizationService;

    @Mock
    private ExternalMonitoringService externalMonitoringService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ReportCommandServiceImpl reportCommandService;

    @Captor
    private ArgumentCaptor<Report> reportCaptor;

    private CreateReportCommand validCommand;
    private final Long expectedPlotId = 10L;
    private final Long expectedOrgId = 1L;

    @BeforeEach
    void setUp() {
        // Inicializamos un comando válido básico para reutilizar en los tests
        validCommand = new CreateReportCommand(
                new ProfileId(5L),
                new PlotId(expectedPlotId),
                new OrganizationId(expectedOrgId),
                ReportType.PARCEL,
                MetricType.TEMPERATURE,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );
    }

    @Test
    @DisplayName("Should successfully create report and calculate correct statistical metrics when readings exist")
    void shouldCreateReportAndCalculateMetricsSuccessfully() {
        // ARRANGE
        when(externalOrganizationService.fetchPlotExists(any(PlotId.class))).thenReturn(true);
        when(reportRepository.existsByPlotIdAndTypeAndReportPeriod(any(), any(), any())).thenReturn(false);

        // Simulamos lecturas de sensores retornadas por el ACL de monitoreo: 10.0, 20.0, 30.0
        List<ReadingResource> mockReadings = List.of(
                new ReadingResource(10.0, "C", LocalDateTime.now()),
                new ReadingResource(20.0, "C", LocalDateTime.now()),
                new ReadingResource(30.0, "C", LocalDateTime.now())
        );
        when(externalMonitoringService.fetchReadingsForReport(eq(expectedPlotId), eq("TEMPERATURE"), any(), any()))
                .thenReturn(mockReadings);

        // Simulamos el guardado asignando un ID al reporte vía reflexión
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            ReflectionTestUtils.setField(report, "id", 100L);
            return report;
        });

        // ACT
        Long reportId = reportCommandService.handle(validCommand);

        // ASSERT
        assertNotNull(reportId);
        assertEquals(100L, reportId);

        // Verificamos y capturamos el Reporte que se mandó a guardar
        verify(reportRepository).save(reportCaptor.capture());
        Report savedReport = reportCaptor.getValue();

        // Validamos que los cálculos estadísticos (Streams) sean exactos
        assertEquals(20.0, savedReport.getMetrics().averageValue(), "El promedio de (10+20+30)/3 debe ser 20.0");
        assertEquals(30.0, savedReport.getMetrics().maxValue(), "El valor máximo debe ser 30.0");
        assertEquals(10.0, savedReport.getMetrics().minValue(), "El valor mínimo debe ser 10.0");
        assertEquals(3, savedReport.getMetrics().dataCount(), "El conteo total de lecturas debe ser 3");
    }

    @Test
    @DisplayName("Should create report with empty metrics (all zeros) when no readings exist for the period")
    void shouldCreateReportWithEmptyMetricsWhenNoReadingsFound() {
        // ARRANGE
        when(externalOrganizationService.fetchPlotExists(any(PlotId.class))).thenReturn(true);
        when(reportRepository.existsByPlotIdAndTypeAndReportPeriod(any(), any(), any())).thenReturn(false);
        when(externalMonitoringService.fetchReadingsForReport(anyLong(), anyString(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            ReflectionTestUtils.setField(report, "id", 101L);
            return report;
        });

        // ACT
        Long reportId = reportCommandService.handle(validCommand);

        // ASSERT
        assertEquals(101L, reportId);
        verify(reportRepository).save(reportCaptor.capture());
        Report savedReport = reportCaptor.getValue();

        // Validamos que use ReportMetrics.empty()
        assertEquals(0.0, savedReport.getMetrics().averageValue());
        assertEquals(0.0, savedReport.getMetrics().maxValue());
        assertEquals(0.0, savedReport.getMetrics().minValue());
        assertEquals(0, savedReport.getMetrics().dataCount());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when period end date is before start date")
    void shouldThrowExceptionWhenPeriodIsInvalid() {
        // ARRANGE
        CreateReportCommand invalidPeriodCommand = new CreateReportCommand(
                new ProfileId(5L),
                new PlotId(expectedPlotId),
                new OrganizationId(expectedOrgId),
                ReportType.PARCEL,
                MetricType.TEMPERATURE,
                LocalDate.of(2026, 12, 31), // Fecha inicio mayor
                LocalDate.of(2026, 1, 1)    // Fecha fin menor
        );

        when(messageSource.getMessage(eq("report.period.invalid"), any(), any(Locale.class)))
                .thenReturn("El periodo del reporte es inválido");

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                reportCommandService.handle(invalidPeriodCommand)
        );

        assertEquals("El periodo del reporte es inválido", exception.getMessage());
        // Verificamos que se aborte antes de llamar a servicios externos o base de datos
        verifyNoInteractions(externalOrganizationService, externalMonitoringService, reportRepository);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the requested plot does not exist")
    void shouldThrowExceptionWhenPlotDoesNotExist() {
        // ARRANGE
        when(externalOrganizationService.fetchPlotExists(any(PlotId.class))).thenReturn(false);
        when(messageSource.getMessage(eq("report.plot.not.found"), any(), any(Locale.class)))
                .thenReturn("La parcela solicitada no existe");

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                reportCommandService.handle(validCommand)
        );

        assertEquals("La parcela solicitada no existe", exception.getMessage());
        verifyNoInteractions(externalMonitoringService);
        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when a report already exists for the same plot, type, and period")
    void shouldThrowExceptionWhenReportIsDuplicate() {
        // ARRANGE
        when(externalOrganizationService.fetchPlotExists(any(PlotId.class))).thenReturn(true);
        when(externalMonitoringService.fetchReadingsForReport(anyLong(), anyString(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Simulamos que el repositorio encuentra un reporte duplicado
        when(reportRepository.existsByPlotIdAndTypeAndReportPeriod(any(), any(), any())).thenReturn(true);

        when(messageSource.getMessage(eq("report.duplicate"), any(), any(Locale.class)))
                .thenReturn("Ya existe un reporte para este periodo");

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                reportCommandService.handle(validCommand)
        );

        assertEquals("Ya existe un reporte para este periodo", exception.getMessage());
        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should catch repository exception and rethrow as IllegalArgumentException with localized message")
    void shouldThrowExceptionWhenRepositoryFailsToSave() {
        // ARRANGE
        when(externalOrganizationService.fetchPlotExists(any(PlotId.class))).thenReturn(true);
        when(reportRepository.existsByPlotIdAndTypeAndReportPeriod(any(), any(), any())).thenReturn(false);
        when(externalMonitoringService.fetchReadingsForReport(anyLong(), anyString(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Simulamos fallo en la base de datos (ej. desconexión o violación de constraint)
        when(reportRepository.save(any(Report.class))).thenThrow(new RuntimeException("Database error"));
        when(messageSource.getMessage(eq("report.save.error"), any(), any(Locale.class)))
                .thenReturn("Error al guardar el reporte");

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                reportCommandService.handle(validCommand)
        );

        assertEquals("Error al guardar el reporte", exception.getMessage());
    }
}