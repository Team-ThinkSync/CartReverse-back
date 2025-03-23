package com.project.webshopproject.ask;

import com.project.webshopproject.ask.entity.Ask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AskRepository extends JpaRepository<Ask, Long> {
    // 사용자 ID로 페이징된 문의사항 조회
    Page<Ask> findByUserId(Long userId, Pageable pageable);

    // 사용자 ID와 문의 ID로 조회
    Optional<Ask> findByIdAndUserId(Long id, Long userId);

    // 사용자 ID로 모든 문의사항 조회 (페이징 없이)
    List<Ask> findByUserId(Long userId);
}

