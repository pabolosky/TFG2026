package com.casino.services;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class rouletteService {
	
	private Random random = new Random();
	
	public int girar() {
		return random.nextInt(37);
	} 
	
	public void apostar() {
		
	}

}
