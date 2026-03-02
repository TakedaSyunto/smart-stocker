package com.example.stocker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.stocker.entity.Stock;
import com.example.stocker.repository.StockRepository;

@SpringBootApplication
public class SmartStockerApplication {

    public static void main(String[] args) {
        // ここを SpringApplication に修正
        SpringApplication.run(SmartStockerApplication.class, args);
    }

    @Bean
    CommandLineRunner sampleData(StockRepository repository) {
        return args -> {
            Stock s = new Stock();
            // Entityで定義したメソッド名（たぶんこれ）に修正
            s.setItemName("テスト用在庫(AWS)"); 
            s.setQuantity(100);
            repository.save(s);
            System.out.println("AWSのDBへデータを保存しました！");
        };
    }
}