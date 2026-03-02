package com.example.stocker.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // 1. 在庫登録時の「計算」用：数値だけを返してもらう
    public double predictDailyRate(String itemName) {
        try {
            String prompt = String.format(
                "商品名「%s」について、1人暮らしでの1日あたりの平均的な消費個数を、" +
                "純粋な数値（float）1つだけで出力してください。単位や説明は一切不要です。" +
                "例：洗剤なら 0.033、お米5kgなら 0.03、卵10個なら 1.0", 
                itemName
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("temperature", 0); // 回答を安定させる
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            HttpEntity<Map<String, Object>> entity = createRequest(requestBody);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            
            String content = extractContent(response);
            return Double.parseDouble(content.trim());
        } catch (Exception e) {
            System.err.println("AI数値予測エラー: " + e.getMessage());
            return 0.1; 
        }
    }

    // 2. フロントエンドの「提案バッジ」用：短い文章でアドバイスしてもらう
    public String predictSuggestion(String itemName) {
        try {
        	String prompt = String.format(
        		    "商品「%s」について、1人暮らしでの消費目安を教えて。" +
        		    "「1日1回の使用で約30日分」のような形式で、20文字以内で答えて。" +
        		    "挨拶や解説は一切不要です。", 
        		    itemName
        		);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            HttpEntity<Map<String, Object>> entity = createRequest(requestBody);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            
            return extractContent(response);
        } catch (Exception e) {
            return "標準的な消費ペースと推測します。";
        }
    }

    // 共通：リクエスト作成
    private HttpEntity<Map<String, Object>> createRequest(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return new HttpEntity<>(body, headers);
    }

    // 共通：レスポンスから文字列を抽出
    private String extractContent(ResponseEntity<Map> response) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        String content = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
        
        // 【追加】AIが実際に何を言っているかコンソールに表示する
        System.out.println("★AIの回答: [" + content + "]"); 
        
        return content;
    }
}