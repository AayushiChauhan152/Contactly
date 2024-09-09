package com.contact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.contact.entity.User;
import com.contact.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String home(Model m) {
		m.addAttribute("title", "Home- a smart contact manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About- a smart contact manager");
		return "about";
	}

	@GetMapping("/signUp")
	public String signUp(Model m) {
		m.addAttribute("title", "register- a smart contact manager");
		m.addAttribute("user", new User());
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute User user, BindingResult res, Model m) {
		m.addAttribute("title", "register- a smart contact manager");

		if (res.hasErrors()) {
			System.out.println(res);
			m.addAttribute("msg", "alert-error");
			m.addAttribute("user", user);
			return "register";
		} else {
			user.setRole("USER");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setEnabled(true);
			user.setImgUrl("contact.png");
			
			System.out.println(user);

			m.addAttribute("msg", "alert-success");
			this.userRepository.save(user);
			m.addAttribute("user", new User());
		}
		return "register";
	}

	
	@GetMapping("/signin")
	public String signIn(Model m) {
		m.addAttribute("title", "login- a smart contact manager");
		return "login";
	}
}
