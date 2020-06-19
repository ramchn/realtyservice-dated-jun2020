package com.realtymgmt.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity 
public class Task {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="native")
	@GenericGenerator(name = "native", strategy = "native")	
	private Integer taskId;
	
	private String taskName;
	
	private String taskDescription;
	
	private String taskStatus;
	
	@ManyToOne
	private Owner owner;
	
	@ManyToOne
	private Tenant tenant;
	
	@ManyToOne
	private Contact contact;
	
	private Date taskCreateddate;
	
	private Date taskModifieddate;
	
	public Task() {
		
	}
	
	public Task(Integer id) {
		taskId = id;
	}
	
	public Integer getTaskId() {
		return taskId;
	}
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getTaskDescription() {
		return taskDescription;
	}
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
	
	public String getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	public Owner getOwner() {
		return owner;
	}
	
	public Tenant getTenant() {
		return tenant;
	}
	
	public Contact getContact() {
		return contact;
	}
	
	public Date getTaskCreateddate() {
		return taskCreateddate;
	}
	public void setTaskCreateddate(Date taskCreateddate) {
		this.taskCreateddate = taskCreateddate;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Date getTaskModifieddate() {
		return taskModifieddate;
	}

	public void setTaskModifieddate(Date taskModifieddate) {
		this.taskModifieddate = taskModifieddate;
	}
	
}
