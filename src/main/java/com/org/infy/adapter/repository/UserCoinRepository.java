package com.org.infy.adapter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.org.infy.adapter.model.Coins;

@Repository
public interface UserCoinRepository extends MongoRepository<Coins, String>{
	public Coins findByEmail(String email);
}
