package com.realtymgmt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Owner {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Integer ownerId;
	
	private String ownerName;
	
	@Column(name="user_email_address", insertable=false, updatable=false)
  	private String emailAddress;
	
	@OneToOne
	private User user;
	
	public Owner() {
		
	}
	
	public Owner(Integer id) {
		ownerId = id;
	}
	
	public Integer getOwnerId() {
		return ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

		
}
