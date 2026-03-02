package com.example.stocker.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.stocker.entity.Stock;
import com.example.stocker.repository.StockRepository;
import com.example.stocker.service.OpenAIService;
import com.example.stocker.service.StockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class StockController {

    private final StockService stockService;
    private final OpenAIService openAIService;
    
    @Autowired
    private StockRepository stockRepository;

    // 1. 在庫一覧
    @GetMapping
    public List<Stock> list() {
        return stockService.getAllStocks();
    }

    // 2. 在庫登録（ここが重複していた箇所です。1つに統合しました）
    @PostMapping
    public Stock create(@RequestBody Stock stock) {
        // 固定値ではなく、OpenAIに消費ペースを聞く！
        double rate = openAIService.predictDailyRate(stock.getItemName()); 
        stock.setDailyConsumptionRate(rate);

        if (stock.getQuantity() != null && rate > 0) {
            long daysLeft = (long) (stock.getQuantity() / rate);
            LocalDate outDate = LocalDate.now().plusDays(daysLeft);
            stock.setEstimatedOutDate(outDate.toString());
        }

        return stockRepository.save(stock);
    }

    // 3. AI予測提案API
    @GetMapping("/predict")
    public Map<String, String> predict(@RequestParam String itemName) {
        Map<String, String> response = new HashMap<>();
        
        // 固定ロジックを捨てて、OpenAIを呼ぶ！
        String suggestion = openAIService.predictSuggestion(itemName);
        
        response.put("suggestion", suggestion);
        return response;
    }

    // 消費ペース判定ロジック
    private double getPredictRate(String name) {
        if (name.contains("洗剤")) return 0.033;
        if (name.contains("トイレットペーパー")) return 0.07;
        return 0.1;
    }

    // 在庫消費（1個減らす）
    @PatchMapping("/{id}/consume")
    public Stock consume(@PathVariable Long id) {
        return stockService.consumeOne(id);
    }
    
    
}