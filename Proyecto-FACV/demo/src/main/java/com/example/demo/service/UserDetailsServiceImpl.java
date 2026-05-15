package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailsServiceImpl - Cargando usuario: {}", username);
        Usuario usuario = usuarioRepository.findByNombre(username);
        if (usuario == null) {
            log.warn("UserDetailsServiceImpl - Usuario no encontrado: {}", username);
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        log.info("UserDetailsServiceImpl - Usuario autenticado: {} con rol {}", username, usuario.getRol());
        // Usuario implementa UserDetails directamente
        return usuario;
    }
}
