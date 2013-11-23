package com.jdbc.dbutils.domain;

import java.io.Serializable;

public class UserInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", username=" + username + ", pwsd="
				+ pswd + "]";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPwsd() {
		return pswd;
	}
	public void setPwsd(String pwsd) {
		this.pswd = pwsd;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String username;
	private String pswd;
	public UserInfo() {
		// TODO Auto-generated constructor stub
	}

}
