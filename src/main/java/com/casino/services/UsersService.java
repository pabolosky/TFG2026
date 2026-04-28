package com.casino.services;

import java.util.List;

import com.casino.model.User;

public interface UsersService {
	void Save(User user);
	void Delete(Integer idUser);
	List<User> SearchAll();
	List<User> SearchRegistered();
	User SearchById(Integer idUser);
	User SearchByUsername(String username);
	int Block(int idUser);
	int Activate(int idUser);
}
