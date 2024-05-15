package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class myConfig {



 
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
   

	@Bean
	public UserDetailsService userDetailsService() {
		

		return new UserDetailsServiceImple();
	}
    
	 @Bean
	  DaoAuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
	        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
	        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
	        return daoAuthenticationProvider;
	    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((Requests)-> Requests
				.requestMatchers("/admin/**")
				.hasRole("ADMIN")
				.requestMatchers("/user/**")
				.hasRole("USER")
				.requestMatchers("/**")
				.permitAll()).formLogin((form->form.
						loginPage("/signin")
						
						.defaultSuccessUrl("/user/index")
						)
						).logout((logout->logout.logoutSuccessUrl("/logout")));
		
	        return http.build();
	}
}
