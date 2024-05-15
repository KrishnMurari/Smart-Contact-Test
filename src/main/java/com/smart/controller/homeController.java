package com.smart.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.userRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


@Controller
public class homeController {
	
	@Autowired
   private  userRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home(Model model) {
		
		return "home";
	}

    @GetMapping("/About")
   	public String About() {
   		
   		return "About";
   	}
    
    @GetMapping("/signup")
    public String signup(Model model) {
    	
    	model.addAttribute("user",new User());
        return "signup";
    }
    
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult res,
    		@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, 
    		 Model model, HttpSession session) {
    	
    	
    	try {
    		
    		
    		if(!agreement) {
        		System.out.println("You have not agreed the term and condition");
        		throw new Exception("You have not agreed the term and condition");
        	}
//    		System.out.println("Error "+ res.toString());
//    		
    		if(res.hasErrors()) {
    			System.out.println("Error "+ res.toString());
    			model.addAttribute("user",user);
    			return "signup";
    		}
    		
    		
        	user.setRole("ROLE_USER");
        	user.setEnabled(true);
        	user.setImageUrl("Default.png");
        	user.setPassword(passwordEncoder.encode(user.getPassword()));
        	
        	
        	System.out.println("Agrement "+ agreement);
        	System.out.println("user "+ user);
        	
        	User result =this.userRepository.save(user);
        	
        	model.addAttribute("user", new User());
        	
        	session.setAttribute("message",new Message("Successfully Registered !!","alert-success"));
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
		    session.setAttribute("message",new Message("something went wrong !! "+e.getMessage(),"alert-danger"));
			return "signup";
		}
      
    }

    @GetMapping("/signin")
   	public String customlogin(Model model) {
   		model.addAttribute("title","login page");
   		return "login";
   	}
    
    @GetMapping("/logout")
   	public String logout() {
   		
   		return "logout";
   	}
}
