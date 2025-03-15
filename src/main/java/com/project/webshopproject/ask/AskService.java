package com.project.webshopproject.ask;

import com.project.webshopproject.ask.dto.AskRequestDto;
import com.project.webshopproject.ask.entity.Ask;
import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AskService {
    private final AskRepository askRepository;
    private final ProductRepository productRepository;

    public Ask createAsk(AskRequestDto askRequest) {
        Product product = productRepository.findById(askRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("해당 Product가 존재하지 않습니다."));

        Ask ask = new Ask(
                askRequest.getUserId(),
                askRequest.getTitle(),
                askRequest.getContent(),
                askRequest.getCategory(),
                product
        );
        return askRepository.save(ask);
    }

    public Ask updateAsk(Long id, AskRequestDto askRequest) {
        Ask ask = askRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        return askRepository.save(ask);
    }

    public void deleteAsk(Long id, Long userID) {
        Ask ask = askRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        if (!ask.getUserId().equals(userID)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        askRepository.deleteById(id);
    }

    public Ask addAdminResponse(Long id, String response) {
        Ask ask = askRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        ask.setAdminResponse(response);
        return askRepository.save(ask);
    }

    public List<Ask> getAsksByUserID(Long userID) {
        return askRepository.findByUserId(userID);
    }

    public AskRequestDto getAskDetail(Long askId, Long userId) {
        Ask ask = getAsksByIdAndUserID(askId, userId);
        return new AskRequestDto(
                userId,
                ask.getTitle(),
                ask.getContent(),
                ask.getCategory(),
                ask.getProduct().getProductId() // 수정된 부분
        );
    }

    // 특정 문의사항 세부 조회
    public Ask getAsksByIdAndUserID(Long id, Long userID) {
        return askRepository.findByIdAndUserId(id, userID)
                .orElseThrow(() -> new RuntimeException("문의사항이 존재하지 않습니다."));
    }

    public Ask addAnswerToAsk(Long id, String answer) {
        Ask ask = askRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의사항이 존재하지 않습니다."));
        ask.setAnswer(answer);
        return askRepository.save(ask);
    }

    public List<Ask> getAllAsks() { // 메서드명 수정
        return askRepository.findAll();
    }
}
