package com.casino.services.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.casino.model.User;
import com.casino.repository.UsersRepository;
import com.casino.services.UsersService;

@Service
public class UsersServiceJPA implements UsersService{
	@Autowired
	private UsersRepository usersRepository;
	
	@Override
	public void Save(User user) {usersRepository.save(user);}

	@Override
	public void Delete(Integer IdUser) {usersRepository.deleteById(IdUser);}

	@Override
	public List<User> SearchAll() {return usersRepository.findAll();}

	@Override
	public List<User> SearchRegistered() {
		return usersRepository.findByRegisterDateNotNull();
	}

	@Override
	public User SearchById(Integer idUser) {
		Optional<User> optional = usersRepository.findById(idUser);
		if (optional.isPresent()) return optional.get();
		return null;
	}

	@Override
	public User SearchByUsername(String username) {
		return usersRepository.findByUsername(username);
	}

	@Override
	public int Block(int idUser) {
		return 0;
	}

	@Override
	public int Activate(int idUser) {
		return 0;
	}


}
