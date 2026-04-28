package com.casino.model.BlackJack;

import java.util.ArrayList;
import java.util.List;

public class BlackJackGame {
	private List<Card> deck;
    private List<Card> playerHand = new ArrayList<>();
    private List<Card> dealerHand = new ArrayList<>();
    private int bet;
    private boolean gameOver;
    private String message;
    
    
	public List<Card> getDeck() {
		return deck;
	}
	public void setDeck(List<Card> deck) {
		this.deck = deck;
	}
	public List<Card> getPlayerHand() {
		return playerHand;
	}
	public void setPlayerHand(List<Card> playerHand) {
		this.playerHand = playerHand;
	}
	public List<Card> getDealerHand() {
		return dealerHand;
	}
	public void setDealerHand(List<Card> dealerHand) {
		this.dealerHand = dealerHand;
	}
	public int getBet() {
		return bet;
	}
	public void setBet(int bet) {
		this.bet = bet;
	}
	public boolean isGameOver() {
		return gameOver;
	}
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    
}
