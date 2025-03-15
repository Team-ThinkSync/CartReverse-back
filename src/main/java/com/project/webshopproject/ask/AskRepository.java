package com.project.webshopproject.ask;

import com.project.webshopproject.ask.entity.Ask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AskRepository extends JpaRepository<Ask, Long> {
    List<Ask> findByUserId(Long userId);
    Optional<Ask> findByIdAndUserId(Long id, Long userId);
}
