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
import com.realtymgmt.entity.Owner;
import com.realtymgmt.entity.Task;
import com.realtymgmt.entity.Tenant;
import com.realtymgmt.repository.ContactRepository;
import com.realtymgmt.repository.OwnerRepository;
import com.realtymgmt.repository.TaskRepository;
import com.realtymgmt.repository.TenantRepository;

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
  private Environment env;
      
  // tenant services
  @GetMapping("/tenant")
  public @ResponseBody String getTenant(@RequestParam(defaultValue = "1") String id) {
	  
	  Optional<Tenant> opt = tenantRepository.findById(Integer.valueOf(id));
	  
	  return opt.isPresent() ? opt.get().toString() : "";
	  	  
  }
  
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
