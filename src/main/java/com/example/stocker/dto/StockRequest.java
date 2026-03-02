package com.example.stocker.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class StockRequest {
    @NotBlank(message = "商品名を入力してください")
    private String itemName;
    private Integer quantity;
    private Integer minQuantity;
}