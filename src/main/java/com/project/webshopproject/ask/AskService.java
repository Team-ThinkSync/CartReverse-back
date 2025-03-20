package com.project.webshopproject.ask;

import com.project.webshopproject.ask.dto.AskRequestDto;
import com.project.webshopproject.ask.dto.AskResponseDto;
import com.project.webshopproject.ask.entity.Ask;
import com.project.webshopproject.ask.entity.AskImage;
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
    public AskResponseDto createAsk(Long userId, AskRequestDto askRequest) {
        Ask ask = new Ask(userId, askRequest); // userId를 DTO에서 추출하는 것이 아니라 직접 주입
        askRepository.save(ask);
        return new AskResponseDto(ask);
    }


    // 문의사항 수정
    public AskResponseDto updateAsk(Long Id, AskRequestDto askRequest) {
        Ask ask = askRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));

        ask.setTitle(askRequest.getTitle());
        ask.setContent(askRequest.getContent());

        Ask updatedAsk = askRepository.save(ask);

        // Ask를 AskResponseDto로 변환해서 반환
        return convertToDto(updatedAsk);
    }

    // 문의사항 삭제
    public void deleteAsk(Long Id, Long userId) {
        Ask ask = askRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        if (!ask.getUserId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        askRepository.deleteById(Id);
    }


    // 사용자 Id로 문의사항 조회
    public List<AskResponseDto> getAsksByUserId(Long userId) {
        return askRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 문의사항 세부 조회
    public AskResponseDto getAskDetail(Long askId, Long userId) {
        return getAsksByIdAndUserId(askId, userId);
    }

    // 특정 문의사항 조회 (Id와 사용자 Id로)
    private AskResponseDto getAsksByIdAndUserId(Long id, Long userId) {
        Ask ask = askRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("문의사항이 존재하지 않습니다."));

        AskResponseDto askResponseDto = new AskResponseDto(ask);
        return askResponseDto; // DTO 변환 후 반환
    }


    // 답변을 추가한 문의사항 반환
    public AskResponseDto addAnswerToAsk(Long Id, String answer, String response) {
        Ask ask = askRepository.findById(Id)
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
