package com.casino.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casino.model.User;
import com.casino.model.BlackJack.Card;
import com.casino.repository.UsersRepository;

@Service
public class blackJackService {

	@Autowired
    private UsersRepository userRepository;

    public List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        
        for (String suit : suits) {
            for (String rank : ranks) {
                int value;
                if (rank.equals("A")) value = 11;
                else if (Arrays.asList("J", "Q", "K").contains(rank)) value = 10;
                else value = Integer.parseInt(rank);
                deck.add(new Card(suit, rank, value));
            }
        }
        Collections.shuffle(deck);
        return deck;
    }

    public int calculateScore(List<Card> hand) {
        int score = 0;
        int aces = 0;
        for (Card card : hand) {
            score += card.getValue();
            if (card.getRank().equals("A")) aces++;
        }
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }
        return score;
    }

    @Transactional
    public void updateMoney(String username, int amount) {
        User user = userRepository.findByUsername(username);
        user.setUserMoney(user.getUserMoney() + amount);
        userRepository.save(user);
    }
   
}
