package com.realtymgmt.controller;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtymgmt.entity.Access;
import com.realtymgmt.entity.Contact;
import com.realtymgmt.entity.User;
import com.realtymgmt.entity.Owner;
import com.realtymgmt.entity.Tenant;
import com.realtymgmt.repository.AccessRepository;
import com.realtymgmt.repository.ContactRepository;
import com.realtymgmt.repository.UserRepository;
import com.realtymgmt.repository.OwnerRepository;
import com.realtymgmt.repository.TenantRepository;

// comment added
@RestController 
public class MainController {
	
  @Autowired 
  private TenantRepository tenantRepository;
  
  @Autowired
  private OwnerRepository ownerRepository;
  
  @Autowired
  private ContactRepository contactRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private AccessRepository accessRepository;
  
  @Autowired
  JdbcTemplate jdbcTemplate;
  
  //un-authenticated home
  @GetMapping("/index")
  public String gindex(@RequestParam(defaultValue = "99") String name) {		
	return "GET " + name;
  }
  
  @PostMapping("/index")
  public String pindex(String name) {		
	return "POST " + name;
  }
  
  //sign up services		 
  // request parameter - owner email
  @PostMapping("/tenantsignup")
  public Integer tenantSignup(@RequestParam(defaultValue = "99") String email) {		
	  
	Owner o = jdbcTemplate.queryForObject("select owner_id from owner where user_email_address = ?", new Object[]{email}, (rs, rowNum) -> 
		new Owner(rs.getInt("owner_id")));
	
	return o.getOwnerId();
  }
  
  // request parameter - user type (owner or contact), name, email, password, services offered (if it is a contact), owner id (if it is a tenant)
  @PostMapping("/signup")
  public User createUser(@RequestParam String usertype, @RequestParam String name, @RequestParam String email, @RequestParam String password, @RequestParam String services, @RequestParam String ownerId) {
	  	  
	  User u = new User();
	  u.setEmailAddress(email);
	  u.setUserPassword(password);
	  userRepository.save(u);
	  
	  	  
	  if(usertype.equals("contact")) {		
		  Contact c = new Contact();
		  c.setContactName(name);
		  c.setServicesOffered(services);
		  c.setUser(u);
		  contactRepository.save(c);
		  
		  Access a = new Access();
		  a.setAuthority("ROLE_CONTACT");
		  a.setUser(u);
		  accessRepository.save(a);
		  
	  } else if (usertype.equals("owner")) {
		  Owner o = new Owner();
		  o.setOwnerName(name);
		  o.setUser(u);
		  ownerRepository.save(o);
		  
		  Access a = new Access();
		  a.setAuthority("ROLE_OWNER");
		  a.setUser(u);
		  accessRepository.save(a);
		  
	  } else if (usertype.equals("tenant")) {
		  
		  Optional<Owner> opt = ownerRepository.findById(Integer.valueOf(ownerId));
		  
		  Tenant t = new Tenant();
		  t.setTenantName(name);
		  t.setUser(u);
		  t.setOwner(opt.isPresent() ? opt.get() : new Owner());	
		  tenantRepository.save(t);
		  
		  Access a = new Access();
		  a.setAuthority("ROLE_TENANT");
		  a.setUser(u);
		  accessRepository.save(a);
		  
	  }
	  
	  return u;
  }  
    
}
