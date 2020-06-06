package com.realtymgmt.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.realtymgmt.entity.Contact;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ContactRepository extends PagingAndSortingRepository<Contact, Integer> {

}
