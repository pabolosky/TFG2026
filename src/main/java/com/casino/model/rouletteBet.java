package com.casino.model;

public class rouletteBet {
	private int quantity;
    private String betType;
    private String betValue;
    
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getBetType() {
		return betType;
	}
	public void setBetType(String betType) {
		this.betType = betType;
	}
	public String getBetValue() {
		return betValue;
	}
	public void setBetValue(String betValue) {
		this.betValue = betValue;
	}
}
