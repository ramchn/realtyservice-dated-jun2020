package com.realtymgmt.repository;

import org.springframework.data.repository.CrudRepository;

import com.realtymgmt.entity.Login;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface LoginRepository extends CrudRepository<Login, Integer> {

}
