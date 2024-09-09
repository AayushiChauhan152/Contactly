package com.contact.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.entity.User;
import com.contact.repository.UserRepository;
import com.contact.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPaswordController {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	Random random = new Random(1000);

	@GetMapping("/forgot-password")
	public String openEmailForm() {
		return "emailForm";
	}

	@PostMapping("/process-password")
	public String sendOTP(@RequestParam String email, Model m,HttpSession s) {

		int otp = random.nextInt(9999);
		String OTP = otp + "";

		System.out.println(otp);

		String subject = "OTP for Password Reset";

		boolean sendEmail = this.emailService.sendEmail(subject, OTP, email);
		System.out.println(sendEmail);
		
		if (sendEmail == false) {
			m.addAttribute("msg", "alert-error");
			return "emailForm";
			
		}else {
			s.setAttribute("otp", otp);
			m.addAttribute("msg", "alert-success");
			s.setAttribute("email", email);
			return "verifyOTP";
		}
	}

	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam int formOTP,HttpSession s,Model m) {
		int my_otp= (int) s.getAttribute("otp");
		String my_email = (String) s.getAttribute("email");
		
		if(my_otp==formOTP) {
			User user = this.userRepository.getUserByUsername(my_email);
			if(user==null) {
				m.addAttribute("msg", "alert-error");
				return "emailForm";
			}
			return "changePassword";
		}else {
			m.addAttribute("msg", "alert-error");
			return "verifyOTP";
		}
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam String password,HttpSession s,Model m) {
		String my_email = (String) s.getAttribute("email");
		User user = this.userRepository.getUserByUsername(my_email);
		
		user.setPassword(this.bCryptPasswordEncoder.encode(password));
		this.userRepository.save(user);
		m.addAttribute("msg", "Your password has been changed successfully!!");
		
		return "login";
	}
	
}
