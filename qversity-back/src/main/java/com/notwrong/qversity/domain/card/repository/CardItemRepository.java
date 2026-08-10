package com.notwrong.qversity.domain.card.repository;

import com.notwrong.qversity.domain.card.entity.CardItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardItemRepository extends JpaRepository<CardItem, Long> {
    List<CardItem> findByDeck_DeckId(Long deckId);
    int countByDeck_DeckId(Long deckId);
}
