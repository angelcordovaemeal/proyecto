package com.bootcamp.movement.infrastructure.controller;

import com.bootcamp.movement.application.service.ReportApplicationService;
import com.bootcamp.movement.domain.model.Movement;
import com.bootcamp.movement.domain.model.report.GeneralReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportApplicationService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldGenerateGeneralReport() throws Exception {

        when(service.generateGeneralReport(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Single.just(GeneralReport.builder().build()));

        mockMvc.perform(
                        get("/api/v1/movements/reports/general")
                                .param("start", "2024-01-01T00:00:00")
                                .param("end", "2024-12-31T23:59:59")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnLast10Movements() throws Exception {

        when(service.last10Movements("acc1"))
                .thenReturn(Single.just(List.of(new Movement())));

        mockMvc.perform(get("/api/v1/movements/reports/last-10/acc1"))
                .andExpect(status().isOk());
    }
}