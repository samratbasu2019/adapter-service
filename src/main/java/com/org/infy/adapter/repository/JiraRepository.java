package com.org.infy.adapter.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.org.infy.adapter.model.JiraTaskStore;

@Repository
public interface JiraRepository extends MongoRepository<JiraTaskStore, String> {
	
}
