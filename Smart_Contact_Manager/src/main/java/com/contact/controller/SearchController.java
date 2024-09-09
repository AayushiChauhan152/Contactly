package com.contact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.contact.entity.Contact;
import com.contact.entity.User;
import com.contact.repository.ContactRepository;
import com.contact.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SearchController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable String query, Principal p) {

		User user = this.userRepository.getUserByUsername(p.getName());
		List<Contact> list = this.contactRepository.findByCnameContainingAndUser(query, user);

		return ResponseEntity.ok(list);
	}

}
