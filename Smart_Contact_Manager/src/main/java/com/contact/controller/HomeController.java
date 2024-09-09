package com.contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.contact.entity.Contact;
import com.contact.entity.User;
import com.contact.repository.ContactRepository;
import com.contact.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@GetMapping("/dashboard")
	public String dashboard(Model m, Principal p,HttpSession session) {

		String name = p.getName();
//		System.out.println(name);
		User user = userRepository.getUserByUsername(name);
//		System.out.println(user);
		m.addAttribute("user", user);
		
		
		String msg = (String) session.getAttribute("msg");
		if (msg != null) {
			m.addAttribute("msg", msg);
			session.removeAttribute("msg"); // Clear the session attribute after using it
		}
		
		return "dashboard";
	}

	@GetMapping("/add-contact")
	public String addContact(Model m, Principal p) {

		m.addAttribute("title", "AddContact- a smart contact manager");

		String name = p.getName();
		User user = userRepository.getUserByUsername(name);
		m.addAttribute("user", user);

		m.addAttribute("contact", new Contact());
		return "addContact";
	}

	@PostMapping("/submit-contact")
	public String submitContact(@ModelAttribute Contact contact, BindingResult res, Principal p, Model m,
			@RequestParam("profile_img") MultipartFile file) {
		m.addAttribute("title", "AddContact- a smart contact manager");

		try {
			String name = p.getName();
			User user = userRepository.getUserByUsername(name);
			m.addAttribute("user", user);

			contact.setUser(user);

			if (!file.isEmpty()) {
				contact.setProfile_img(file.getOriginalFilename());

				File file2 = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.print(file.getOriginalFilename());

			} else {
				contact.setProfile_img("contact.png");
			}

			user.getContacts().add(contact);

			this.userRepository.save(user);
//			System.out.println(contact);

			m.addAttribute("msg", "alert-success");
			return "addContact";

		} catch (Exception e) {

			System.out.print(e);
			m.addAttribute("msg", "alert-error");
			return "addContact";
		}
	}

	@GetMapping("/view-contacts/{page}")
	public String viewContacts(@PathVariable Integer page, Model m, Principal p, HttpSession session) {
		String name = p.getName();
		User user = userRepository.getUserByUsername(name);
		m.addAttribute("user", user);

		String msg = (String) session.getAttribute("msg");
		if (msg != null) {
			m.addAttribute("msg", msg);
			session.removeAttribute("msg"); 
		}

//		(cur page ,contact per page )
		Pageable pageable = PageRequest.of(page, 3);

//		method-1

//		List<Contact>contacts=user.getContacts();
//		m.addAttribute("contacts", contacts);

//		method-2

		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("cur_page", page);
		m.addAttribute("totol_page", contacts.getTotalPages());

		return "viewContacts";
	}

	@GetMapping("/contact/{cid}")
	public String viewParticular(@PathVariable Integer cid, Model m, Principal p) {
		String name = p.getName();
		User user = userRepository.getUserByUsername(name);
		m.addAttribute("user", user);

		Optional<Contact> byId = this.contactRepository.findById(cid);
		Contact contact = byId.get();

		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("contact", contact);
			m.addAttribute("title", contact.getCname());
		} else {
			return "error";
		}
		return "Particular_contact";
	}

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable Integer cid, Model m, Principal p, HttpSession s) {
		String name = p.getName();
		User user = userRepository.getUserByUsername(name);
		m.addAttribute("user", user);

		Contact contact = this.contactRepository.findById(cid).get();
		contact.setUser(null);

		this.contactRepository.delete(contact);

		s.setAttribute("msg", "Contact deleted successfully!");

		return "redirect:/user/view-contacts/0";
	}

	@PostMapping("/update/{cid}")
	public String updateContact(@PathVariable Integer cid, Model m, Principal p) {
		m.addAttribute("title", "update");
		String name = p.getName();
		User user = userRepository.getUserByUsername(name);
		m.addAttribute("user", user);

		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute("contact", contact);

		return "updateContact";
	}

	@PostMapping("/process-update")
	public String processUpdate(@ModelAttribute Contact contact, BindingResult res, Principal p, Model m,
			@RequestParam("profile_img") MultipartFile file, @RequestParam("c_id") Integer id) {

		m.addAttribute("title", "update");
		String name = p.getName();
		User user = this.userRepository.getUserByUsername(name);
		m.addAttribute("user", user);

		try {
			Contact existed = this.contactRepository.findById(id).get();

			if (!file.isEmpty()) {
				contact.setProfile_img(file.getOriginalFilename());

				File file2 = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.print(file.getOriginalFilename());

			} else {
				contact.setProfile_img(existed.getProfile_img());
			}

			contact.setUser(user);
			contact.setCid(id);
			this.contactRepository.save(contact);

			return "redirect:/user/contact/" + contact.getCid();

		} catch (Exception e) {

			System.out.print(e);
			m.addAttribute("msg", "alert-error");
			return "addContact";
		}

	}

	@GetMapping("/profile")
	public String viewUserProfile(Model m, Principal p) {
		String name = p.getName();
		User user = userRepository.getUserByUsername(name);
		m.addAttribute("user", user);
		return "profile";
	}

	@GetMapping("/setting")
	public String userSetting(Model m, Principal p) {
		m.addAttribute("title", "setting");
		String name = p.getName();
		User user = userRepository.getUserByUsername(name);
		m.addAttribute("user", user);
		return "setting";
	}
	
	@PostMapping("/doSetting")
	public String processSetting(@RequestParam("oldPassword") String opswd , @RequestParam("newPassword") String npswd,Model m, Principal p,HttpSession s) {
		m.addAttribute("title", "setting");
		String name = p.getName();
		User user = userRepository.getUserByUsername(name);
		m.addAttribute("user", user);
		
		System.out.print(opswd);
		System.out.print(npswd);
		
		if(this.bCryptPasswordEncoder.matches(opswd, user.getPassword())) {
			
			user.setPassword(this.bCryptPasswordEncoder.encode(npswd));
			this.userRepository.save(user);
			s.setAttribute("msg", "your password has been changed successfully!!");
			return "redirect:/user/dashboard";
		}else {
			m.addAttribute("msg", "alert-error");
			return "setting";
		}
	}
	
	

}
