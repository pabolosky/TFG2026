package com.casino.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casino.model.User;

public interface UsersRepository extends JpaRepository<User, Integer>{
	User findByUsername(String username);
	List<User> findByRegisterDateNotNull();
}
