package com.casino.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.casino.model.User;
import com.casino.model.BlackJack.BlackJackGame;
import com.casino.repository.UsersRepository;
import com.casino.services.UsersService;
import com.casino.services.blackJackService;
import com.casino.services.plinkoService;
import com.casino.services.slotsService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping(value = "/games")
public class gamesController {
	
	@Autowired
	private UsersRepository userRepository;
	
	@Autowired
	private plinkoService plinkoService;
	
	
	@Autowired
    private slotsService slotsService;
	
	@Autowired
	private blackJackService bjService;


	@GetMapping("/poker")
	public String mostrarPoker(Model model) {
		
		return "games/poker";
	}
	
	@GetMapping("/roulette")
	public String mostrarRuleta(Model model) {
		
		return "games/roulette";
	}
	
/********************************************************SLOTS********************************************************/
	
	@GetMapping("/slots")
    public String mostrarSlots(Model model) {
        User user = slotsService.obtenerUsuarioAutenticado();
        model.addAttribute("saldo", (user != null) ? user.getUserMoney() : 0);
        return "games/slots";
    }

    @PostMapping("/slots/spin")
    @ResponseBody
    public Map<String, Object> spin(@RequestParam int apuesta) {
        Map<String, Object> response = new HashMap<>();
        try {
            String[] resultado = slotsService.girar();
            Map<String, Integer> datos = slotsService.procesarGiro(resultado, apuesta);
            
            response.put("ok", true);
            response.put("resultado", resultado);
            response.put("premio", datos.get("premio"));
            response.put("saldo", datos.get("nuevoSaldo")); 
        } catch (Exception e) {
            response.put("ok", false);
            response.put("error", e.getMessage());
        }
        return response;
    }
	
	
/******************************************************PLINKO**********************************************************/	
	

    @GetMapping("/plinko")
    public String mostrarPlinko(Model model) {
        User user = plinkoService.obtenerUsuarioAutenticado();
        model.addAttribute("saldo", (user != null) ? user.getUserMoney() : 0);
        return "games/plinko";
    }

    @PostMapping("/plinko/drop")
    @ResponseBody
    public Map<String, Object> drop(@RequestParam int apuesta) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> datos = plinkoService.procesarCaida(apuesta);
            response.put("ok", true);
            response.put("slot", datos.get("slot"));
            response.put("mult", datos.get("mult"));
            response.put("premio", datos.get("premio"));
            response.put("saldo", datos.get("nuevoSaldo"));
        } catch (Exception e) {
            response.put("ok", false);
            response.put("error", e.getMessage());
        }
        return response;
    }
/**************************************************BLACKJACK**************************************************************/	
    @GetMapping("/blackjack")
    public String blackjackIndex(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("money", user.getUserMoney());
        return "games/blackjack-index";
    }
    
    @GetMapping("/blackjack/play")
    public String playBlackjack(HttpSession session, Model model, Principal principal) {
        BlackJackGame game = (BlackJackGame) session.getAttribute("blackjackGame");       

        if (game == null) {
            return "redirect:/games/blackjack";
        }
       
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("money", user.getUserMoney());

        model.addAttribute("game", game);
        model.addAttribute("playerScore", bjService.calculateScore(game.getPlayerHand()));
        
        return "games/blackjack"; 
    }


    @PostMapping("/blackjack/start")
    public String startBlackjack(@RequestParam int bet, HttpSession session, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        
        if (bet > user.getUserMoney() || bet <= 0) {
            return "redirect:/games/blackjack?error=insufficient_funds";
        }
   
        bjService.updateMoney(principal.getName(), -bet);

        BlackJackGame game = new BlackJackGame();
        game.setDeck(bjService.createDeck());
        game.setBet(bet);
        
        game.getPlayerHand().add(game.getDeck().remove(0));
        game.getDealerHand().add(game.getDeck().remove(0));
        game.getPlayerHand().add(game.getDeck().remove(0));
        game.getDealerHand().add(game.getDeck().remove(0));

        session.setAttribute("blackjackGame", game);
        return "redirect:/games/blackjack/play";
    }
    
    @PostMapping("/blackjack/hit")
    public String blackjackHit(HttpSession session) {
        BlackJackGame game = (BlackJackGame) session.getAttribute("blackjackGame");
        if (game != null && !game.isGameOver()) {
            game.getPlayerHand().add(game.getDeck().remove(0));
            
            if (bjService.calculateScore(game.getPlayerHand()) > 21) {
                game.setGameOver(true);
                game.setMessage("¡Te has pasado!. El dealer gana.");
            }
        }
        return "redirect:/games/blackjack/play";
    }
    
    @PostMapping("/blackjack/stand")
    public String blackjackStand(HttpSession session, Principal principal) {
        BlackJackGame game = (BlackJackGame) session.getAttribute("blackjackGame");
        if (game == null || game.isGameOver()) return "redirect:/games/blackjack/play";

        while (bjService.calculateScore(game.getDealerHand()) < 17) {
            game.getDealerHand().add(game.getDeck().remove(0));
        }

        int pScore = bjService.calculateScore(game.getPlayerHand());
        int dScore = bjService.calculateScore(game.getDealerHand());

        if (dScore > 21 || pScore > dScore) {
            game.setMessage("¡¡Ganaste!! " + (game.getBet() * 2));
            bjService.updateMoney(principal.getName(), game.getBet() * 2);
        } else if (pScore < dScore) {
            game.setMessage("El Dealer gana esta vez.");
        } else {
            game.setMessage("Empate. Se te devuelve la apuesta.");
            bjService.updateMoney(principal.getName(), game.getBet());
        }
        
        game.setGameOver(true);
        return "redirect:/games/blackjack/play";
    }

}
