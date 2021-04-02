package com.prodev.bloggingservice.auth;

public class AuthorityDTO {

	public AuthorityDTO() {

	}

	public AuthorityDTO(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
