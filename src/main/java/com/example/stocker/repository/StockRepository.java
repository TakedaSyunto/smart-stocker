package com.example.stocker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.stocker.entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    // 必要に応じてキーワード検索などを追加予定
}