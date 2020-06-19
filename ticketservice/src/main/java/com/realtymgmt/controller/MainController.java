package com.realtymgmt.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtymgmt.alert.Email;
import com.realtymgmt.entity.Contact;
import com.realtymgmt.entity.Owner;
import com.realtymgmt.entity.Task;
import com.realtymgmt.entity.Tenant;
import com.realtymgmt.repository.ContactRepository;
import com.realtymgmt.repository.OwnerRepository;
import com.realtymgmt.repository.TaskRepository;
import com.realtymgmt.repository.TenantRepository;

// comment added
@RestController 
public class MainController {
	
  @Autowired 
  private TenantRepository tenantRepository;

  @Autowired
  private TaskRepository taskRepository;
  
  @Autowired
  private OwnerRepository ownerRepository;
  
  @Autowired
  private ContactRepository contactRepository;
  
  @Autowired
  private Environment env;
  
  @Autowired
  JdbcTemplate jdbcTemplate;
    
  //authenticated home
  @GetMapping("/home")
  public String ghome() {		
	return "GET home";
  }
  
  @PostMapping("/home")
  public String phome() {		
	return "POST home";
  }
  
  // tenant services
  // request parameter - tenant email address
  @PostMapping("/tenant/tasks")
  public Map<String, Object> tenantHome(@RequestParam String email) {
	
	Tenant t = jdbcTemplate.queryForObject("select tenant_id from tenant where user_email_address = ?", new Object[]{email}, (rs, rowNum) -> 
			new Tenant(rs.getInt("tenant_id")));
	
	Optional<Tenant> opt = tenantRepository.findById(Integer.valueOf(t.getTenantId()));
	Tenant tenant = opt.isPresent() ? opt.get() : new Tenant();	  
	
	List<Task> talst = jdbcTemplate.queryForList("select task_id from task where tenant_tenant_id = ?", new Object[]{t.getTenantId()}, Task.class);
	
	List<Integer> ids = new ArrayList<>();
	
	for(Task ta : talst) {
		ids.add(ta.getTaskId());
	}
		
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("tenant", tenant);
	map.put("tasks", taskRepository.findAllById(ids));
		
	return map;
  }
  
  // request parameter - tenant email address, task name, task description
  @PostMapping("/tenant/savetask")
  public Task createTask(@RequestParam String taskName, @RequestParam String taskDescription, @RequestParam String email) throws AddressException, MessagingException, IOException {
	  
	  Task t = new Task();
	  
	  t.setTaskName(taskName);
	  t.setTaskDescription(taskDescription);
	  
	  t.setTaskStatus("New");	  
	  t.setTaskCreateddate(new Date());
	  
	  Tenant te = jdbcTemplate.queryForObject("select tenant_id from tenant where user_email_address = ?", new Object[]{email}, (rs, rowNum) -> 
		new Tenant(rs.getInt("tenant_id")));
	  
	  Optional<Tenant> optTenant = tenantRepository.findById(Integer.valueOf(te.getTenantId()));
	  Tenant tenant = optTenant.isPresent() ? optTenant.get() : new Tenant();
			  
	  t.setTenant(tenant);
	  t.setOwner(tenant.getOwner());
	  
	  taskRepository.save(t);
	  
	  Email.getInstance(env).sendOwnerMail(t);
	  
    return t;
  }
  
  // Owner Services
  // request parameter - owner email address
  @PostMapping("/owner/tasks")
  public Map<String, Object> ownerHome(@RequestParam String email) {		
	
	Owner o = jdbcTemplate.queryForObject("select owner_id from owner where user_email_address = ?", new Object[]{email}, (rs, rowNum) -> 
		new Owner(rs.getInt("owner_id")));

	Optional<Owner> opt = ownerRepository.findById(Integer.valueOf(o.getOwnerId()));
	Owner owner = opt.isPresent() ? opt.get() : new Owner();
	
	List<Tenant> telst = jdbcTemplate.queryForList("select tenant_id from tenant where owner_owner_id = ?", new Object[]{o.getOwnerId()}, Tenant.class);
	
	List<Integer> teids = new ArrayList<>();
	
	for(Tenant te : telst) {
		teids.add(te.getTenantId());
	}
	
	Iterable<Tenant> tenants = tenantRepository.findAllById(teids);
	
	Map<Tenant, Iterable<Task>> tenantTasks = new HashMap<Tenant, Iterable<Task>>();
	
	for(Tenant tenant : tenants) {
		
		List<Task> talst = jdbcTemplate.queryForList("select task_id from task where tenant_tenant_id = ?", new Object[]{tenant.getTenantId()}, Task.class);
		
		List<Integer> ids = new ArrayList<>();
		
		for(Task ta : talst) {
			ids.add(ta.getTaskId());
		}
		
		tenantTasks.put(tenant, taskRepository.findAllById(ids));
		
	}		
	
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("owner", owner);
	map.put("tenantTasks", tenantTasks);	
		
	return map;
  }
  
  @GetMapping("/owner/contacts")
  public Iterable<Contact> contacts() {
	  
	  return contactRepository.findAll();
  }
  
  // request parameter task id, contact id
  @PostMapping("/owner/assigncontacttotask")
  public Task assignContactToTask(@RequestParam String taskId, @RequestParam String contactId) throws AddressException, MessagingException, IOException {
	  	    
	  Optional<Task> optTask = taskRepository.findById(Integer.valueOf(taskId));
	  Optional<Contact> optContact = contactRepository.findById(Integer.valueOf(contactId));
	  
	  Task t = optTask.isPresent() ? optTask.get() : new Task();
	  t.setTaskStatus("Assigned");
	  t.setContact(optContact.isPresent() ? optContact.get() : new Contact());
	  t.setTaskModifieddate(new Date());
	  
	  taskRepository.save(t);
	  
	  Email.getInstance(env).sendContactMail(t);
	  	  
	  return t;
  }
  
 
  // contact services
  // request parameter - contact email
  @PostMapping("/contact/tasks")
  public Map<String, Object> contactHome(@RequestParam String email) {		
	
	Contact c = jdbcTemplate.queryForObject("select contact_id from contact where user_email_address = ?", new Object[]{email}, (rs, rowNum) -> 
		new Contact(rs.getInt("contact_id")));

	Optional<Contact> opt = contactRepository.findById(Integer.valueOf(c.getContactId()));
	Contact contact = opt.isPresent() ? opt.get() : new Contact();
			
	List<Task> talst = jdbcTemplate.queryForList("select task_id from task where contact_contact_id = ?", new Object[]{c.getContactId()}, Task.class);
	
	List<Integer> ids = new ArrayList<>();
	
	for(Task ta : talst) {
		ids.add(ta.getTaskId());
	}
			
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("contact", contact);
	map.put("tasks", taskRepository.findAllById(ids));
		
	return map;
  }
    
  // task services
  // request parameter - task id
  @GetMapping("/task")
  public Task task(@RequestParam(defaultValue = "1") String taskId) {
	  
	  Optional<Task> opt = taskRepository.findById(Integer.valueOf(taskId));
	  
	  Task task = opt.isPresent() ? opt.get() : new Task();
	 	  
	  return task;
  }
  
  // owner or contact service
  // request parameter - task id
  @PostMapping("/task/closetask")
  public Task closeTask(@RequestParam String taskId) throws AddressException, MessagingException, IOException {
	  
	  Optional<Task> opt = taskRepository.findById(Integer.valueOf(taskId));	
	  
	  Task t = opt.isPresent() ? opt.get() : new Task();
	  t.setTaskStatus("Closed");
	  t.setTaskModifieddate(new Date());
	  
	  taskRepository.save(t);
	  
	  Email.getInstance(env).sendTenantMail(t);
	  	 	  
	  return t;
  }
    
}
