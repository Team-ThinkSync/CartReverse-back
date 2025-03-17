package com.project.webshopproject.ask;

import com.project.webshopproject.ask.dto.AskRequestDto;
import com.project.webshopproject.ask.dto.AskResponseDto;
import com.project.webshopproject.ask.entity.Ask;
import com.project.webshopproject.ask.entity.AskImage;
import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AskService {

    private final AskRepository askRepository;
    private final ProductRepository productRepository;

    // 문의사항 생성
    public AskResponseDto createAsk(AskRequestDto askRequest) {
        Product product = productRepository.findById(askRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("해당 Product가 존재하지 않습니다."));

        Ask ask = new Ask(
                askRequest.getUserId(),
                askRequest.getTitle(),
                askRequest.getContent(),
                askRequest.getCategory(),
                product
        );
        Ask savedAsk = askRepository.save(ask);

        // Ask를 AskResponseDto로 변환해서 반환
        return convertToDto(savedAsk);
    }

    // 문의사항 수정
    public AskResponseDto updateAsk(Long id, AskRequestDto askRequest) {
        Ask ask = askRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));

        ask.setTitle(askRequest.getTitle());
        ask.setContent(askRequest.getContent());

        Ask updatedAsk = askRepository.save(ask);

        // Ask를 AskResponseDto로 변환해서 반환
        return convertToDto(updatedAsk);
    }

    // 문의사항 삭제
    public void deleteAsk(Long id, Long userID) {
        Ask ask = askRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        if (!ask.getUserId().equals(userID)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        askRepository.deleteById(id);
    }


    // 사용자 ID로 문의사항 조회
    public List<AskResponseDto> getAsksByUserID(Long userID) {
        return askRepository.findByUserId(userID)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 문의사항 세부 조회
    public AskResponseDto getAskDetail(Long askId, Long userId) {
        Ask ask = getAsksByIdAndUserID(askId, userId);
        return convertToDto(ask);
    }

    // 특정 문의사항 조회 (ID와 사용자 ID로)
    private Ask getAsksByIdAndUserID(Long id, Long userID) {
        return askRepository.findByIdAndUserId(id, userID)
                .orElseThrow(() -> new RuntimeException("문의사항이 존재하지 않습니다."));
    }

    // 답변을 추가한 문의사항 반환
    public AskResponseDto addAnswerToAsk(Long id, String answer, String response) {
        Ask ask = askRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의사항이 존재하지 않습니다."));

        ask.setAnswer(answer, response);
        Ask updatedAsk = askRepository.save(ask);

        return convertToDto(updatedAsk);
    }

    // 전체 문의사항 조회
    public List<AskResponseDto> getAllAsks() {
        List<Ask> asks = askRepository.findAll();
        return asks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Ask 엔티티를 AskResponseDto로 변환하는 메소드
    private AskResponseDto convertToDto(Ask ask) {
        List<String> imageUrls = ask.getImages().stream()
                .map(AskImage::getImageUrl)
                .collect(Collectors.toList());

        return new AskResponseDto(
                ask.getAskId(),
                ask.getUserId(),
                ask.getTitle(),
                ask.getContent(),
                ask.getCategory(),
                ask.getProduct().getProductId(),
                ask.getAnswer(),
                imageUrls
        );
    }

}
