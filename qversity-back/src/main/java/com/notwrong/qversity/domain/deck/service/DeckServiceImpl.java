package com.notwrong.qversity.domain.deck.service;

import com.notwrong.qversity.domain.card.repository.CardItemRepository;
import com.notwrong.qversity.domain.deck.dto.DeckRequest;
import com.notwrong.qversity.domain.deck.dto.DeckResponse;
import com.notwrong.qversity.domain.deck.entity.Deck;
import com.notwrong.qversity.domain.deck.repository.DeckRepository;
import com.notwrong.qversity.domain.user.entity.User;
import com.notwrong.qversity.domain.user.repository.UserRepository;
import com.notwrong.qversity.global.exception.BaseException;
import com.notwrong.qversity.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final CardItemRepository cardItemRepository;

    @Override
    public List<DeckResponse> getMyDecks(Long userId) {
        return deckRepository.findByOwner_UserId(userId).stream()
                .map(d -> {
                    int count = cardItemRepository.countByDeck_DeckId(d.getDeckId());
                    return DeckResponse.from(d).withTotalCards(count);
                })
                .collect(Collectors.toList());
    }

    @Override
    public DeckResponse getDeck(Long deckId, Long userId) {
        Deck deck = findById(deckId);
        if (!deck.isPublic() && !deck.getOwner().getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.DECK_ACCESS_DENIED);
        }
        int count = cardItemRepository.countByDeck_DeckId(deckId);
        return DeckResponse.from(deck).withTotalCards(count);
    }

    @Override
    @Transactional
    public DeckResponse createDeck(Long userId, DeckRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Deck deck = Deck.builder()
                .owner(user)
                .name(request.getName())
                .description(request.getDescription())
                .isPublic(request.isPublic())
                .build();
        return DeckResponse.from(deckRepository.save(deck));
    }

    @Override
    @Transactional
    public DeckResponse updateDeck(Long deckId, Long userId, DeckRequest request) {
        Deck deck = findById(deckId);
        if (!deck.getOwner().getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.DECK_ACCESS_DENIED);
        }
        deck.setName(request.getName());
        deck.setDescription(request.getDescription());
        deck.setPublic(request.isPublic());
        return DeckResponse.from(deckRepository.save(deck));
    }

    @Override
    @Transactional
    public void deleteDeck(Long deckId, Long userId) {
        Deck deck = findById(deckId);
        if (!deck.getOwner().getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.DECK_ACCESS_DENIED);
        }
        deckRepository.delete(deck);
    }

    @Override
    public Deck findById(Long deckId) {
        return deckRepository.findById(deckId)
                .orElseThrow(() -> new BaseException(ErrorCode.DECK_NOT_FOUND));
    }
}
