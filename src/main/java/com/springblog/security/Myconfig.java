package com.springblog.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.springblog.Service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class Myconfig{
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/", "/mylogin", "/article/**", "/useradd","/style.css",
	                "/img/**", "/home", "/css/**", "/js/**", "/reg","/register","/public/**"
	            ).permitAll()
	            .requestMatchers("/admin/**").hasRole("ADMIN")
	            .anyRequest().authenticated()
	        )
	        .userDetailsService(userDetailsService)
	        .formLogin(form -> form
	            .loginPage("/mylogin")
	            .loginProcessingUrl("/login")
//	            .usernameParameter("email")   // 👈 ADD THIS
	            .defaultSuccessUrl("/", true)
	            .failureUrl("/mylogin?error=true")
	            .permitAll()
	        )
	        .logout(logout -> logout
	                .logoutUrl("/logout")
	                .logoutSuccessUrl("/")
	                .permitAll()
	            );
	    return http.build();
	}

	 	@Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
}
