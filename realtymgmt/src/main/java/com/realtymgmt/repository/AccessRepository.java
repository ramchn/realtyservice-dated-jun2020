package com.realtymgmt.repository;

import org.springframework.data.repository.CrudRepository;

import com.realtymgmt.entity.Access;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface AccessRepository extends CrudRepository<Access, Integer> {

}
