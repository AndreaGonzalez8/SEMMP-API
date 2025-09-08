package com.semmp.api.repository;

import com.semmp.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Método para login: busca usuario por email y contraseña
    Usuario findByEmailAndContrasena(String email, String contrasena);

    // Si más adelante se quiere validar si ya existe un email
    boolean existsByEmail(String email);
}
