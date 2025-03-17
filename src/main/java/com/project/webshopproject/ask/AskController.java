package com.project.webshopproject.ask;

import com.project.webshopproject.ask.dto.AskRequestDto;
import com.project.webshopproject.ask.dto.AskResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/asks")
public class AskController {

    private final AskService askService;

    // 특정 문의사항 상세 조회
    @GetMapping("/{askId}")
    public ResponseEntity<AskResponseDto> getAsk(@PathVariable Long askId, @RequestParam Long userId) {
        AskResponseDto askResponse = askService.getAskDetail(askId, userId);
        return ResponseEntity.ok(askResponse);
    }

    // 사용자 문의사항 전체 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getAllAsksByUser(@PathVariable Long userId) {
        List<AskResponseDto> asks = askService.getAsksByUserID(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "문의내용 가져오기 성공");
        response.put("data", asks);

        return ResponseEntity.ok(response);
    }

    // 문의사항 생성
    @PostMapping
    public ResponseEntity<AskResponseDto> createInquiry(@RequestBody AskRequestDto inquiryRequest) {
        AskResponseDto createdAsk = askService.createAsk(inquiryRequest);
        return ResponseEntity.ok(createdAsk);
    }

    // 문의사항 수정
    @PatchMapping("/{askId}")
    public ResponseEntity<AskResponseDto> updateInquiry(@PathVariable Long askId, @RequestBody AskRequestDto inquiryRequest) {
        AskResponseDto updatedAsk = askService.updateAsk(askId, inquiryRequest);
        return ResponseEntity.ok(updatedAsk);
    }

    // 문의사항 삭제
    @DeleteMapping("/{askId}")
    public ResponseEntity<Map<String, String>> deleteInquiry(@PathVariable Long askId, @RequestBody Map<String, String> requestBody) {
        Long userId = Long.parseLong(requestBody.get("userID"));
        askService.deleteAsk(askId, userId);

        Map<String, String> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "삭제 완료");

        return ResponseEntity.ok(response);
    }

    // 답변 추가
    @PatchMapping("/{askId}/response")
    public ResponseEntity<Map<String, Object>> addAnswerToInquiry(@PathVariable Long askId, @RequestBody Map<String, String> requestBody) {
        String answer = requestBody.get("answer");
        String response = requestBody.get("response");

        if (answer == null || answer.isEmpty()) {
            throw new RuntimeException("답변 내용을 입력해주세요.");
        }

        AskResponseDto updatedAsk = askService.addAnswerToAsk(askId, answer, response);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("askId", updatedAsk.getAskId());
        responseMap.put("userId", updatedAsk.getUserId());
        responseMap.put("title", updatedAsk.getTitle());
        responseMap.put("content", updatedAsk.getContent());
        responseMap.put("category", updatedAsk.getCategory());
        responseMap.put("productId", updatedAsk.getProductId());
        responseMap.put("imageUrl", updatedAsk.getImageUrls());
        responseMap.put("answer", updatedAsk.getAnswer());

        return ResponseEntity.ok(responseMap);
    }
}
