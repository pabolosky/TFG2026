package com.casino.services;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.casino.model.User;
import com.casino.repository.UsersRepository;

@Service
public class slotsService {

    @Autowired
    private UsersRepository usersRepository;

    private String[] simbolos = {"🍒", "🍋", "🔔", "⭐", "💎"};
    private Random random = new Random();

    public String[] girar() {
        String[] resultado = new String[3];
        for (int i = 0; i < 3; i++) {
            resultado[i] = simbolos[random.nextInt(simbolos.length)];
        }
        return resultado;
    }

    // Método para obtener el usuario que ha iniciado sesión
    public User obtenerUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return usersRepository.findByUsername(username);
        }
        return null;
    }

    @Transactional
    public Map<String, Integer> procesarGiro(String[] r, int apuesta) {
        User user = obtenerUsuarioAutenticado();
        
        if (user == null) throw new IllegalStateException("Usuario no encontrado");
        if (apuesta < 10 || apuesta > 1000) throw new IllegalArgumentException("Apuesta no permitida");
        if (user.getUserMoney() < apuesta) throw new IllegalStateException("Saldo insuficiente");

        // Lógica de premios
        int premio = 0;
        if (r[0].equals(r[1]) && r[1].equals(r[2])) {
            premio = apuesta * 10;
        } else if (r[0].equals(r[1]) || r[1].equals(r[2]) || r[0].equals(r[2])) {
            premio = apuesta * 2;
        }

        // Actualización de saldo en el objeto y la BD
        int nuevoSaldo = user.getUserMoney() - apuesta + premio;
        user.setUserMoney(nuevoSaldo);
        usersRepository.save(user);

        // Devolvemos los datos necesarios
        Map<String, Integer> resultados = new HashMap<>();
        resultados.put("premio", premio);
        resultados.put("nuevoSaldo", nuevoSaldo);
        return resultados;
    }
}