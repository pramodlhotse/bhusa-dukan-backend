package com.bhusadukan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateWeighingRequest(

        @DecimalMin(
                value = "0.001",
                message = "Actual weight must be greater than zero"
        )
        BigDecimal actualWeight,

        @Size(
                max = 100,
                message = "Weighing machine ID must not exceed 100 characters"
        )
        String weighingMachineId,

        @DecimalMin(
                value = "0.001",
                message = "Machine reading must be greater than zero"
        )
        BigDecimal weighingMachineReading,

        Long verifiedBy,

        @Size(
                max = 500,
                message = "Remarks must not exceed 500 characters"
        )
        String remarks
) {
}