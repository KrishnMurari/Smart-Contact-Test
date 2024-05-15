package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.userRepository;
import com.smart.entities.User;
import com.smart.entities.contact;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private userRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
	    
		
		String userName=principal.getName();
		
		User user=userRepository.getUserByUserName(userName);
		
		model.addAttribute("user",user);
		
	}
	
//	DashBoard Home
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
	
		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
		
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title","add contact");
		model.addAttribute("contact", new contact());
		
		return "normal/add_contact_form";
	}
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute contact contact,
			@RequestParam("profileImage") MultipartFile file,  
			Principal principal, HttpSession session) {
		
		try {
			
			String name=principal.getName();
			User user=this.userRepository.getUserByUserName(name);
			
			if(file.isEmpty()) {
				System.out.println("File Empty");
				
				contact.setImage("contact.png");
				
			}else {
				
				contact.setImage(file.getOriginalFilename());
				File savefile=new ClassPathResource("static/img").getFile();
				
				Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("Image Updaloaded");
			}
			
			
			user.getContacts().add(contact);
			contact.setUser(user);
			
			
			this.userRepository.save(user);
			
			
			System.out.println("Data "+contact);
			
			System.out.println("Added to data base");
			
			session.setAttribute("message", new Message("Your contact is added !!","success"));
			
		} catch (Exception e) {
			
			System.out.println("Error "+e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went worng try aggain !!","danger"));
			
		}
		return "normal/add_contact_form";
		
	}

	
	@RequestMapping("/show-contacts")
	public String showContact(Model model,Principal principal) {
		
		model.addAttribute("title","show user contacts");

		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);

		List<contact> contacts= this.contactRepository.findContactByUser(user.getId());
		
		model.addAttribute("contacts",contacts);
		
		return "normal/show_contacts";
	}
	
	
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		
		Optional<contact> contactptional=this.contactRepository.findById(cId);
	    
		contact contact=contactptional.get();
		
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) {
		
	          model.addAttribute("contact",contact);
	    
		}
	    
		return "normal/contact_detail";
	}
	
	
	@RequestMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid")Integer cId, Model model, Principal principal,HttpSession session) {
		
		Optional<contact> contactptional=this.contactRepository.findById(cId);
		
		contact contact=contactptional.get();
		
		contact.setUser(null);
		
		this.contactRepository.delete(contact);  
		session.setAttribute("message",new Message("Contact deleted succesfully...","success"));

	   
		return "redirect:/user/show-contacts";
	}
	
	
	
	
	
	
}
