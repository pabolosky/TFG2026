package com.casino.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.casino.model.User;
import com.casino.repository.UsersRepository;

@Service
public class plinkoService {

    @Autowired
    private UsersRepository usersRepository;

    private final double[] MULTIPLIERS = {10, 3, 1.5, 0.5, 0.3, 0.5, 1.5, 3, 10};
    private final int SLOTS = 9;
    private final Random random = new Random();

    public User obtenerUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return usersRepository.findByUsername(((UserDetails) principal).getUsername());
        }
        return null;
    }

    // Simula la caída de la bola por el tablero de pines
    public int calcularSlot() {
        int pos = 0;
        for (int i = 0; i < 8; i++) {
            pos += random.nextBoolean() ? 1 : 0;
        }
        return pos;
    }

    @Transactional
    public Map<String, Object> procesarCaida(int apuesta) {
        User user = obtenerUsuarioAutenticado();
        if (user == null) throw new IllegalStateException("Usuario no encontrado");
        if (apuesta < 10 || apuesta > 1000) throw new IllegalArgumentException("Apuesta no permitida");
        if (user.getUserMoney() < apuesta) throw new IllegalStateException("Saldo insuficiente");

        int slot = calcularSlot();
        double mult = MULTIPLIERS[slot];
        int premio = (int) Math.round(apuesta * mult);
        // Si mult < 1 el premio será menor que la apuesta, así que también "pierde" parte
        int nuevoSaldo = user.getUserMoney() - apuesta + premio;
        user.setUserMoney(nuevoSaldo);
        usersRepository.save(user);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("slot", slot);
        resultado.put("mult", mult);
        resultado.put("premio", premio - apuesta); // ganancia neta (puede ser negativa)
        resultado.put("nuevoSaldo", nuevoSaldo);
        return resultado;
    }
}