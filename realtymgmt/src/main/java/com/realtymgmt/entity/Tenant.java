package com.realtymgmt.entity;

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
  	private Login login;
  
  	@ManyToOne
  	private Owner owner;
  

	public Integer getTenantId() {
		return tenantId;
	}
		
	
	public String getTenantName() {
		return tenantName;
	}
	
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	
	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
    public String toString() { 
        return String.format("Tenant Id = " + getTenantId() + "; Tenant Name = " + getTenantName() + "; Tenant Email = " + login.getEmailAddress() + "; Owner  = " + getOwner()); 
    } 
  	
}
