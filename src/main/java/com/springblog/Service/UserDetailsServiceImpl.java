package com.springblog.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springblog.Entities.User;
import com.springblog.Repository.UserRepo;
import com.springblog.security.CustomUserDetails;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepo repo; 
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.getUserByUserName(username);
		if(user==null) {
			throw new UsernameNotFoundException("User does not exist");
		}
		CustomUserDetails userdet = new CustomUserDetails(user);
		return userdet;
	}

}
