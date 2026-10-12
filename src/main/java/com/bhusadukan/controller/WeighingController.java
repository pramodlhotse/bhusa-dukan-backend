package com.bhusadukan.controller;

import com.bhusadukan.dto.CreateWeighingRequest;
import com.bhusadukan.dto.WeighingResponse;
import com.bhusadukan.service.WeighingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
@Tag(
        name = "Weighing",
        description = "APIs for weighing ordered Bhusa"
)
public class WeighingController {

    private final WeighingService weighingService;

    @Operation(
            summary = "Record actual weight",
            description = "Records actual weight for an order item and calculates the final amount"
    )
    @PostMapping("/{orderItemId}/weighing")
    @ResponseStatus(HttpStatus.CREATED)
    public WeighingResponse createWeighing(
            @PathVariable Long orderItemId,
            @Valid @RequestBody CreateWeighingRequest request) {

        return weighingService.createWeighing(
                orderItemId,
                request
        );
    }
}