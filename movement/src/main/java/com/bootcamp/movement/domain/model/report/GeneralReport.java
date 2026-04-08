package com.bootcamp.movement.domain.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralReport {

    private List<BankAccountReport> accounts;
    private List<CreditReport> credits;
}

