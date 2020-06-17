package com.realtymgmt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Contact {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Integer contactId;
	
	private String contactName;
	
	@OneToOne
	private User user;
	
	@Column(name="user_email_address", insertable=false, updatable=false)
  	private String emailAddress;
	
	private String servicesOffered;
	
	public Contact() {
		
	}

	public Contact(Integer id) {
		contactId = id;
	}
	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
		
	public String getServicesOffered() {
		return servicesOffered;
	}

	public void setServicesOffered(String servicesOffered) {
		this.servicesOffered = servicesOffered;
	}

	public Integer getContactId() {
		return contactId;
	}
	
	
	
}
