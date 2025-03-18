package com.project.webshopproject.ask;

import com.project.webshopproject.ask.dto.AskRequestDto;
import com.project.webshopproject.ask.dto.AskResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/asks")
public class AskController {

    private final AskService askService;

    // 특정 문의사항 상세 조회
    @GetMapping("/{askId}")
    public ResponseEntity<AskResponseDto> getAsk(@PathVariable Long askId, @AuthenticationPrincipal Long userId) {
        AskResponseDto askResponse = askService.getAskDetail(askId, userId);
        return ResponseEntity.ok(askResponse);
    }

    // 사용자 문의사항 전체 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AskResponseDto>> getAllAsksByUser(@PathVariable Long userId) {
        List<AskResponseDto> asks = askService.getAsksByUserId(userId);
        return ResponseEntity.ok(asks);
    }

    // 문의사항 생성
    @PostMapping
    public ResponseEntity<AskResponseDto> createAsk(@RequestBody AskRequestDto askRequest) {
        AskResponseDto createdAsk = askService.createAsk(askRequest);
        return ResponseEntity.ok(createdAsk);
    }

    // 문의사항 수정
    @PatchMapping("/{askId}")
    public ResponseEntity<AskResponseDto> updateAsk(@PathVariable Long askId, @RequestBody AskRequestDto askRequest) {
        AskResponseDto updatedAsk = askService.updateAsk(askId, askRequest);
        return ResponseEntity.ok(updatedAsk);
    }

    // 문의사항 삭제
    @DeleteMapping("/{askId}")
    public ResponseEntity<String> deleteAsk(@PathVariable Long askId, @AuthenticationPrincipal Long userId) {
        askService.deleteAsk(askId, userId);
        return ResponseEntity.ok("삭제 완료");
    }

    // 답변 추가
    @PatchMapping("/{askId}/response")
    public ResponseEntity<AskResponseDto> addAnswerToAsk(@PathVariable Long askId, @RequestBody Map<String, String> requestBody) {
        String answer = requestBody.get("answer");
        String response = requestBody.get("response");

        if (answer == null || answer.isEmpty()) {
            throw new RuntimeException("답변 내용을 입력해주세요.");
        }

        AskResponseDto updatedAsk = askService.addAnswerToAsk(askId, answer, response);

        return ResponseEntity.ok(updatedAsk);
    }
}
