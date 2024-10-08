package com.contact.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entity.Contact;
import com.contact.entity.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

//	pagination method
	
	@Query("from Contact as c where c.user.id = :userId")
	public Page<Contact> findContactByUser(@Param("userId") int userId,Pageable pageable);
	
	public List<Contact> findByCnameContainingAndUser(String cname,User user);

}
