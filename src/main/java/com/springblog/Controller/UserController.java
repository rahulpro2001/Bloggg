package com.springblog.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import com.springblog.Repository.UserRepo;

@Controller
public class UserController {
	
	  @Autowired
	    private PasswordEncoder encoder;
		@Autowired
		private UserRepo userRepo;


}
