package com.casino.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.casino.model.Statuses;
import com.casino.model.User;
import com.casino.services.UsersService;


import jakarta.servlet.http.HttpServletRequest; 

@Controller
@RequestMapping("/authentication")
public class authenticationController {
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private PasswordEncoder passwordEncoder; 
	
	@GetMapping("/register")
	public String register(User user) {
		return "authentication/registerForm";
	}
	
	@PostMapping("/saveRegister")
	public String saveRegister(User user, RedirectAttributes attributes) {
		String PlainPwd = user.getUserPassword();
		String EncodedPwd = passwordEncoder.encode(PlainPwd);
		user.setUserPassword(EncodedPwd);
		user.setUserStatus(3);
		user.setRegisterDate(new Date());
		user.setUserMoney(1000);
		user.setActive(true);
		
		List<Statuses> roles = new ArrayList<>();
		Statuses defaultStatus = new Statuses();
		defaultStatus.setId(1);
	    roles.add(defaultStatus);
	    user.setStatuses(roles);
	    
		usersService.Save(user);
		attributes.addFlashAttribute("msg", "Has sido registrado. ¡Ya puedes ingresar a nuestro casino!");
		 
		return "redirect:/autentication/login";
	}
	 
	@GetMapping("/login")
	public String login() {
		return "authentication/loginForm";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.logout(request, null, null);
		return "redirect:/";
	}    	 
	
	@GetMapping("usersList")
	public String userList(Model model) {
		List<User> lista = usersService.SearchAll();
		model.addAttribute("users", lista);
		return "authentication/usersList";
	}
	
	@GetMapping("/usersList/activar/{id}")
	public String activateUser(@PathVariable("id") int id) {
	    User user = usersService.SearchById(id);
	    user.setActive(true);
	    usersService.Save(user);
	    return "redirect:/authentication/usersList"; 
	}

	@GetMapping("/usersList/desactivar/{id}")
	public String desactivarUsuario(@PathVariable("id") int id) {
	    User user = usersService.SearchById(id);
	    user.setActive(false);
	    usersService.Save(user);
	    return "redirect:/authentication/usersList";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
    
	@GetMapping("/bcrypt/{texto}")
    @ResponseBody
   	public String encriptar(@PathVariable("texto") String texto) {    	
   		return texto + " Encriptado en Bcrypt: " + passwordEncoder.encode(texto);
   	}
	
}
