package com.realtymgmt.repository;

import org.springframework.data.repository.CrudRepository;

import com.realtymgmt.entity.Owner;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface OwnerRepository extends CrudRepository<Owner, Integer> {

}
