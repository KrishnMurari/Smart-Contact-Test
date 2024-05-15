package com.smart.dao;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.contact;

public interface ContactRepository extends JpaRepository<contact, Integer> {

	@Query("from contact as c where c.user.id =:userId")
	public List<contact> findContactByUser(@Param("userId")int userId);
	
	
}
