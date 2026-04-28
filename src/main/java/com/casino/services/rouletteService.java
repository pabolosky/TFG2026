package com.casino.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.casino.model.User;
import com.casino.model.rouletteBet;
import com.casino.repository.UsersRepository;

@Service
public class rouletteService {
	@Autowired
	private UsersRepository usersRepository;
	
	private static final int[] numbers = {
			0, 32, 15 ,19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36,
	        11, 30, 8, 23,10, 5, 24, 16, 33, 1, 20, 14, 31, 9,
	        22, 18, 29, 7, 28, 12, 35, 3, 26
	};
	
	private static final int[] red = {
	        1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36
	};
	
	private Random random = new Random();
	
	public Map<String, Object> girar(List<rouletteBet> apuestas) {
		int index = random.nextInt(numbers.length);
		int number = numbers[index];
		String colour = "";
		colour = colour(number);
		
		Map<String, Object> resultado = new HashMap<>(); 
		resultado.put("number",  number);
		resultado.put("colour", colour);
		resultado.put("index", index);
		for(rouletteBet bet : apuestas) {
			procesarApuesta(bet.getQuantity(), bet.getBetType(), bet.getBetValue(), number, colour);
		}
		return resultado;
	}   
	
	public String colour(int number) {
		if(number == 0) return "green";
		for (int r  : red) {
			if(number == r) return "red";
		}
		return "black";
	}
	
	public int procesarApuesta(int dineroApostado, String tipoApuesta, String valorApostado, int numeroGanador, String colorGanador) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	User user = usersRepository.findByUsername(((UserDetails)principal).getUsername());
	    int premio = 0;

	    switch (tipoApuesta.toLowerCase()) {
	        case "colour":
	            if (colorGanador.equalsIgnoreCase(valorApostado)) { 
	                premio = dineroApostado * 2;
	            }else {
	            	premio -= dineroApostado;
	            }
	            break;

	        case "parity":
	            boolean esPar = numeroGanador != 0 && numeroGanador % 2 == 0;
	            if ((valorApostado.equals("even") && esPar) || (valorApostado.equals("odd") && !esPar)) {
	                premio = dineroApostado * 2;
	            }else {
	            	premio -= dineroApostado;
	            }
	            break;

	        case "range": 
	            if (estaEnRango(numeroGanador, valorApostado)) {
	                premio = dineroApostado * 2;
	            }
	            else {
	            	premio -= dineroApostado;
	            }
	            break;

	        case "number":
	            if (numeroGanador == Integer.parseInt(valorApostado)) {
	                premio = dineroApostado * 36; 
	            }
	            else {
	            	premio -= dineroApostado;
	            }
	            break;
	    }  

	    user.setUserMoney(user.getUserMoney() + premio);
	    usersRepository.save(user);
	    
	    return premio;
	}
	
	private boolean estaEnRango(int numero, String rango) {
	    if (numero == 0) return false; 
	    String[] partes = rango.split("-");
	    int min = Integer.parseInt(partes[0]);
	    int max = Integer.parseInt(partes[1]);
	    return numero >= min && numero <= max;
	}
}