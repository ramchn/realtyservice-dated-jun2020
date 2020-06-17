package com.realtymgmt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity // This tells Hibernate to make a table out of this class
public class Tenant {
  
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
  	private Integer tenantId;

  	private String tenantName;

  	@OneToOne
  	private User user;
  
  	@ManyToOne
  	private Owner owner;
  	
  	@Column(name="user_email_address", insertable=false, updatable=false)
  	private String emailAddress;
  
  	public Tenant() {

  	}
  	
  	public Tenant(Integer id) {
  		tenantId = id;
  	}

	public Integer getTenantId() {
		return tenantId;
	}
		
	
	public String getTenantName() {
		return tenantName;
	}
	
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
		
	public void setUser(User user) {
		this.user = user;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}
  	
}
