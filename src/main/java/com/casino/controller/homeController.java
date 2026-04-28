package com.casino.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.casino.model.User; 
import com.casino.services.UsersService;

@Controller
public class homeController {
	@Autowired
	UsersService usersService;

	@GetMapping("/")
    public String home(Model model, Principal principal) {
		if (principal != null) { 
	        model.addAttribute("username", principal.getName());
	        model.addAttribute("isLoggedIn", true);
	    } else {  
	        model.addAttribute("isLoggedIn", false);
	    }
		
		User user = usersService.SearchByUsername(principal.getName());
        model.addAttribute("userStatus", user.getStatuses());
		
        return "home";
    }
	
}

