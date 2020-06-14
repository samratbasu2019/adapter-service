package com.org.infy.adapter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.org.infy.adapter.model.ICountStore;

@Repository
public interface ICountRepository extends MongoRepository<ICountStore, String> {
	
	public List<ICountStore> findByEmailAndIstask(String email,String istask);
}
