package com.example.stocker.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stocker.entity.Stock;
import com.example.stocker.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @Transactional
    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    // 「運用」で一番使う：在庫を1つ減らすメソッド
    @Transactional
    public Stock consumeOne(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("在庫が見つかりません"));
        
        if (stock.getQuantity() > 0) {
            stock.setQuantity(stock.getQuantity() - 1);
            // ここで将来的に「消費ログ」を記録する処理を追加します
        }
        return stockRepository.save(stock);
    }
    
    public void updateEstimatedOutDate(Stock stock) {
        // 例：過去の消費ログから計算するロジック（後ほど実装）
        // 現時点では「1日1個消費」と仮定して、残り個数分の日にちを足す
    	stock.setEstimatedOutDate(LocalDate.now().plusDays(stock.getQuantity()).toString());
        stockRepository.save(stock);
    }
}