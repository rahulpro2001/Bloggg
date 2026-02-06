package com.springblog.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.springblog.Entities.User;

public interface UserRepo extends CrudRepository<User, Integer>{

//	matching with the emtity name User not with the table name buser
	@Query("Select u from User u where u.username=:username")
	public User getUserByUserName(@Param("username") String username);
}
