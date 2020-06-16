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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.realtymgmt.alert.Email;
import com.realtymgmt.entity.Access;
import com.realtymgmt.entity.Contact;
import com.realtymgmt.entity.User;
import com.realtymgmt.entity.Owner;
import com.realtymgmt.entity.Task;
import com.realtymgmt.entity.Tenant;
import com.realtymgmt.repository.AccessRepository;
import com.realtymgmt.repository.ContactRepository;
import com.realtymgmt.repository.UserRepository;
import com.realtymgmt.repository.OwnerRepository;
import com.realtymgmt.repository.TaskRepository;
import com.realtymgmt.repository.TenantRepository;

// comment added
@Controller 
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
  private UserRepository userRepository;
  
  @Autowired
  private AccessRepository accessRepository;
  
  @Autowired
  private Environment env;
  
  @Autowired
  JdbcTemplate jdbcTemplate;
  
  //index
  @GetMapping("/index")
  public String index(Model model) {		
		
	return "index";
  }
    
  //sign up services
  @GetMapping("/signup")
  public String signupForm(Model model, @RequestParam(defaultValue = "99") String ownerId) {		
	
	Optional<Owner> opt = ownerRepository.findById(Integer.valueOf(ownerId));
		
	model.addAttribute("owner", opt.isPresent() ? opt.get() : new Owner());
	
	return "signupform";
  }
  
  @PostMapping("/signup")
  public String createUser(Model model, String usertype, String name, String email, String password, String services, String ownerId) {
	  
	  
	  User u = new User();
	  u.setEmailAddress(email);
	  u.setUserPassword(new BCryptPasswordEncoder().encode(password));
	  userRepository.save(u);
	  model.addAttribute("user", u);
	  	  
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
	  
	  return "usercreated";
  }  
  
  //sign in services
  @GetMapping("/signin")
  public String signinForm(Model model) {		
		
	return "signinform";
  }
  
  //authenticated home
  @GetMapping("/home")
  public String home(Model model) {		
	return "home";
  }
  
  
  // tenant services
  @GetMapping("/tenant/home")
  public String tenant(Model model, @RequestParam(defaultValue = "one@one.one") String email) {
	
	Tenant t = jdbcTemplate.queryForObject("select tenant_id from tenant where user_email_address = ?", new Object[]{email}, (rs, rowNum) -> 
			new Tenant(rs.getInt("tenant_id")));
	
	Optional<Tenant> opt = tenantRepository.findById(Integer.valueOf(t.getTenantId()));
	Tenant tenant = opt.isPresent() ? opt.get() : new Tenant();	  
	
	List<Task> talst = jdbcTemplate.queryForList("select task_id from task where tenant_tenant_id = ?", new Object[]{t.getTenantId()}, Task.class);
	
	List<Integer> ids = new ArrayList<>();
	
	for(Task ta : talst) {
		ids.add(ta.getTaskId());
	}
		
	model.addAttribute("tenant", tenant);
	model.addAttribute("tasks", taskRepository.findAllById(ids));
		
	return "tenanthome";
  }
  
  @GetMapping("/tenant/createtask")
  public String taskForm(Model model, @RequestParam(defaultValue = "1") String tenantId) {
	
	Optional<Tenant> opt = tenantRepository.findById(Integer.valueOf(tenantId));
	
	model.addAttribute("tenant", opt.isPresent() ? opt.get() : new Tenant());
	model.addAttribute("task", new Task());
	
    return "taskform";
  }
  
  @PostMapping("/tenant/savetask")
  public String createTask(Model model, @ModelAttribute Task task, String tenantId, String ownerId) throws AddressException, MessagingException, IOException {
	  
	  Task t = new Task();
	  
	  t.setTaskName(task.getTaskName());
	  t.setTaskDescription(task.getTaskDescription());
	  
	  t.setTaskStatus("New");	  
	  t.setTaskCreateddate(new Date());
	  
	  Optional<Tenant> optTenant = tenantRepository.findById(Integer.valueOf(tenantId));
	  Optional<Owner> optOwner = ownerRepository.findById(Integer.valueOf(ownerId));
	  
	  t.setOwner(optOwner.isPresent() ? optOwner.get() : new Owner());
	  t.setTenant(optTenant.isPresent() ? optTenant.get() : new Tenant());
	  
	  taskRepository.save(t);
	  
	  Email.getInstance(env).sendOwnerMail(t);
	  
	  model.addAttribute("task", t);
    return "tasksaved";
  }
  
  // Owner Services
  @GetMapping("/owner/home")
  public String owner(Model model, @RequestParam(defaultValue = "one@one.one") String email) {		
	
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
		
	model.addAttribute("owner", owner);
	model.addAttribute("tenantTasks", tenantTasks);
		
	return "ownerhome";
  }
  
  @GetMapping("owner/assigncontact")
  public String contactForm(Model model, @RequestParam(defaultValue = "1") String taskId) {
	  
	  Optional<Task> opt = taskRepository.findById(Integer.valueOf(taskId));	
	  	  	  
	  model.addAttribute("task", opt.isPresent() ? opt.get() : new Task());
	  model.addAttribute("contacts", contactRepository.findAll());
	  
	  return "contactform";
  }
  
  @PostMapping("/owner/updatetask")
  public String updateTask(Model model, String taskId, String contactId) throws AddressException, MessagingException, IOException {
	  	    
	  Optional<Task> optTask = taskRepository.findById(Integer.valueOf(taskId));
	  Optional<Contact> optContact = contactRepository.findById(Integer.valueOf(contactId));
	  
	  Task t = optTask.isPresent() ? optTask.get() : new Task();
	  t.setTaskStatus("Assigned");
	  t.setContact(optContact.isPresent() ? optContact.get() : new Contact());
	  t.setTaskModifieddate(new Date());
	  
	  taskRepository.save(t);
	  
	  Email.getInstance(env).sendContactMail(t);
	  
	  model.addAttribute("task", t);
	  
    return "taskupdated";
  }
  
 
  // contact services
  @GetMapping("/contact/home")
  public String contact(Model model, @RequestParam(defaultValue = "one@one.one") String email) {		
	
	Contact c = jdbcTemplate.queryForObject("select contact_id from contact where user_email_address = ?", new Object[]{email}, (rs, rowNum) -> 
		new Contact(rs.getInt("contact_id")));

	Optional<Contact> opt = contactRepository.findById(Integer.valueOf(c.getContactId()));
	Contact contact = opt.isPresent() ? opt.get() : new Contact();
			
	List<Task> talst = jdbcTemplate.queryForList("select task_id from task where contact_contact_id = ?", new Object[]{c.getContactId()}, Task.class);
	
	List<Integer> ids = new ArrayList<>();
	
	for(Task ta : talst) {
		ids.add(ta.getTaskId());
	}
		
	model.addAttribute("contact", contact);
	model.addAttribute("tasks", taskRepository.findAllById(ids));
	
		
	return "contacthome";
  }
  
  @GetMapping("contact/taketask")
  public String viewTask(Model model, @RequestParam(defaultValue = "1") String taskId) {
	  
	  Optional<Task> opt = taskRepository.findById(Integer.valueOf(taskId));	
	  	  	  
	  model.addAttribute("task", opt.isPresent() ? opt.get() : new Task());
	 	  
	  return "taskdetail";
  }
  
  @PostMapping("contact/closetask")
  public String closeTask(Model model, String taskId) throws AddressException, MessagingException, IOException {
	  
	  Optional<Task> opt = taskRepository.findById(Integer.valueOf(taskId));	
	  
	  Task t = opt.isPresent() ? opt.get() : new Task();
	  t.setTaskStatus("Closed");
	  t.setTaskModifieddate(new Date());
	  
	  taskRepository.save(t);
	  
	  Email.getInstance(env).sendOwnerTenantMail(t);
	  
	  model.addAttribute("task", t);
	 	  
	  return "taskclosed";
  }
    
}
