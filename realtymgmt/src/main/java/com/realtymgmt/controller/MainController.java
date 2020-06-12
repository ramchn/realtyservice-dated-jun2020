package com.realtymgmt.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtymgmt.alert.Email;
import com.realtymgmt.entity.Contact;
import com.realtymgmt.entity.Login;
import com.realtymgmt.entity.Owner;
import com.realtymgmt.entity.Task;
import com.realtymgmt.entity.Tenant;
import com.realtymgmt.repository.ContactRepository;
import com.realtymgmt.repository.LoginRepository;
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
  private LoginRepository loginRepository;
  
  @Autowired
  private Environment env;
  
  //sign up services
  @GetMapping("/signup")
  public String signupForm(Model model, @RequestParam(defaultValue = "99") String ownerId) {		
	
	Optional<Owner> opt = ownerRepository.findById(Integer.valueOf(ownerId));
		
	model.addAttribute("owner", opt.isPresent() ? opt.get() : new Owner());
	
	return "signupform";
  }
  
  @PostMapping("/signup")
  public String createUser(Model model, String usertype, String name, String email, String password, String services, String ownerId) {
	  
	  
	  Login l = new Login();
	  l.setEmailAddress(email);
	  l.setLoginPassword(password);
	  loginRepository.save(l);
	  model.addAttribute("login", l);
	  	  
	  if(usertype.equals("contact")) {		
		  Contact c = new Contact();
		  c.setContactName(name);
		  c.setServicesOffered(services);
		  c.setLogin(l);
		  contactRepository.save(c);
		  
	  } else if (usertype.equals("owner")) {
		  Owner o = new Owner();
		  o.setOwnerName(name);
		  o.setLogin(l);
		  ownerRepository.save(o);
		  
	  } else if (usertype.equals("tenant")) {
		  
		  Optional<Owner> opt = ownerRepository.findById(Integer.valueOf(ownerId));
		  
		  Tenant t = new Tenant();
		  t.setTenantName(name);
		  t.setLogin(l);
		  t.setOwner(opt.isPresent() ? opt.get() : new Owner());	
		  tenantRepository.save(t);
		  
	  }
	  
	  return "usercreated";
  }      
  // tenant services
  @GetMapping("/tenant/all")
  public @ResponseBody Iterable<Tenant> getAllTenants() {
	  
    return tenantRepository.findAll();
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
