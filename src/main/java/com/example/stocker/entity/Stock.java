package com.example.stocker.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "商品名は必須です")
    private String itemName;

    @Min(value = 0, message = "在庫数は0以上で入力してください")
    private Integer quantity;

    private Integer minQuantity; // 補充の目安となる下限数

    private LocalDate lastPurchasedDate; // 最終購入日

    private String estimatedOutDate; // 在庫切れ予測日（Java側で計算して格納）
    
    private Double dailyConsumptionRate; // 1日あたりの消費量（例: 0.033）
    
    // 後ほどSecurityを本格導入する際にUserと紐付けます
    // private Long userId; 
}