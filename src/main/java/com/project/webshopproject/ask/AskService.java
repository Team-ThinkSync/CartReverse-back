package com.project.webshopproject.service;

import com.project.webshopproject.model.Ask;
import com.project.webshopproject.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.project.webshopproject.dto.AskRequestDto;
import com.project.webshopproject.repository.AskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AskService {
    private final AskRepository askRepo;

    public Ask createAsk(AskRequestDto askRequest) {
        Ask ask = new Ask(
                askRequest.getUserID(),
                askRequest.getTitle(),
                askRequest.getContent(),
                askRequest.getCategory(),
                askRequest.getItemId()
        );
        return askRepo.save(ask);
    }

    public Ask updateAsk(Long id, AskRequestDto askRequest) {
        Ask ask = askRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        return askRepo.save(ask);
    }

    public void deleteAsk(Long id, Long userID) {
        Ask ask = askRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        if (!ask.getUserId().equals(userID)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        askRepo.deleteById(id);
    }

    public Ask addAdminResponse(Long id, String responese) {
        Ask ask = askRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        ask.setAdminResponse(responese);
        return askRepo.save(ask);
    }

    public List<Ask> getAsksByUserID(Long userID) {
        return askRepo.findByUserID(userID);
    }

    public AskRequestDto getAskDetail(Long askId, Long userId) {
        Ask asks = getAsksByIdAndUserID(askId, userId);
        return new AskRequestDto(
                userId,
                "문의내용 가져오기 성공",
                asks.getTitle(),
                asks.getContent(),
                asks.getCategory(),
                asks.getItemId()
        );
    }

    // 특정 문의사항 세부 조회
    public Ask getAsksByIdAndUserID(Long id, Long userID) {
        return askRepo.findByIdAndUserID(id, userID)
                .orElseThrow(() -> new RuntimeException("문의사항이 존재하지 않습니다."));
    }

    public Ask addAnswerToAsk(Long id, String answer) {
        // ID로 문의사항 조회
        Ask ask = askRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("문의사항이 존재하지 않습니다."));

        // 답변 설정
        ask.setAnswer(answer);

        // 저장 후 반환
        return askRepo.save(ask);
    }

    public List<Ask> getAllasks() {
        return askRepo.findAll();
    }
}
