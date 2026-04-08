package com.bootcamp.movement.infrastructure.controller;

import com.bootcamp.movement.application.messages.AppMessages;
import com.bootcamp.movement.application.service.ReportApplicationService;
import com.bootcamp.movement.domain.model.Movement;
import com.bootcamp.movement.domain.model.report.GeneralReport;
import com.bootcamp.movement.domain.model.response.SuccessResponse;
import io.reactivex.rxjava3.core.Single;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movements/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportApplicationService service;

    @GetMapping("/general")
    public Single<ResponseEntity<SuccessResponse<GeneralReport>>> generalReport(
            @RequestParam String start,
            @RequestParam String end,
            HttpServletRequest req) {

        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);

        return service.generateGeneralReport(startDate, endDate)
                .map(report ->
                        ResponseEntity.ok(
                                SuccessResponse.of(
                                        report,
                                        AppMessages.GENERAL_REPORT_GENERATED,
                                        req.getRequestURI(),
                                        200
                                )
                        )
                );
    }

    @GetMapping("/last-10/{productId}")
    public Single<ResponseEntity<SuccessResponse<List<Movement>>>> last10(
            @PathVariable String productId,
            HttpServletRequest req) {

        return service.last10Movements(productId)
                .map(list ->
                        ResponseEntity.ok(
                                SuccessResponse.of(
                                        list,
                                        AppMessages.LAST_10_MOVEMENTS_RETRIEVED,
                                        req.getRequestURI(),
                                        200
                                )
                        )
                );
    }
}