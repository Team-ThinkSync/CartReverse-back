package com.project.webshopproject.ask;

import com.project.webshopproject.ask.dto.AskRequestDto;
import com.project.webshopproject.ask.dto.AskResponseDto;
import com.project.webshopproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/asks")
public class AskController {

    private final AskService askService;

    // 특정 문의사항 상세 조회
    @GetMapping("/{askId}")
    public ResponseEntity<AskResponseDto> getAsk(@PathVariable Long askId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        AskResponseDto askResponse = askService.getAskDetail(askId, userId);
        return ResponseEntity.ok(askResponse);
    }

    // 사용자 문의사항 전체 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AskResponseDto>> getAllAsksByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,   // 기본 페이지 번호
            @RequestParam(defaultValue = "10") int size) { // 기본 페이지 사이즈

        Pageable pageable = PageRequest.of(page, size);
        Page<AskResponseDto> asks = askService.getAsksByUserId(userId, pageable);
        return ResponseEntity.ok(asks);
    }


    // 문의사항 생성
    @PostMapping
    public ResponseEntity<AskResponseDto> createAsk(
            @AuthenticationPrincipal UserDetailsImpl userDetails, // JWT에서 유저 정보 추출
            @RequestBody AskRequestDto askRequest) {

        Long userId = userDetails.getUser().getUserId(); // JWT에서 가져온 userId
        AskResponseDto createdAsk = askService.createAsk(userId, askRequest); // userId를 따로 전달

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
    public ResponseEntity<String> deleteAsk(@PathVariable Long askId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
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
