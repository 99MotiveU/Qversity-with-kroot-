package com.notwrong.qversity.domain.deck.repository;

import com.notwrong.qversity.domain.deck.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    List<Deck> findByOwner_UserId(Long userId);
    List<Deck> findByIsPublicTrue();
}
