package com.springblog.DTO;

public class Userdto {


	private int user_id;
	private String username;
	private String role;
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Userdto(int user_id, String username, String role) {
		super();
		this.user_id = user_id;
		this.username = username;
		this.role = role;
	}
	public Userdto() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Userdto [user_id=" + user_id + ", username=" + username + ", role=" + role + "]";
	}
	
	
}
