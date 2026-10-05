package com.bhusadukan.controller;

import com.bhusadukan.dto.ProductResponse;
import com.bhusadukan.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(
        name = "Products",
        description = "APIs for managing Bhusa products"
)
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Get all active products",
            description = "Returns all active Bhusa products with current price and available stock"
    )
    @GetMapping
    public List<ProductResponse> getProducts() {
        return productService.getActiveProducts();
    }

    @Operation(
            summary = "Get product by ID",
            description = "Returns product details including current price and available stock"
    )
    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }
}