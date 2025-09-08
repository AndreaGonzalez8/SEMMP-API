package com.semmp.api.controller;

import com.semmp.api.model.Usuario;
import com.semmp.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/usuarios")

public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ---------------------- CREAR USUARIO ----------------------
    @PostMapping
    public ResponseEntity<Map<String, String>> crearUsuario(@RequestBody Usuario usuario) {
        Map<String, String> response = new HashMap<>();
        try {
            // Validar si el correo ya existe
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                response.put("status", "error");
                response.put("message", "El correo ya está registrado.");
                return ResponseEntity.badRequest().body(response);
            }
            usuarioRepository.save(usuario);
            response.put("status", "ok");
            response.put("message", "Usuario registrado exitosamente.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error al registrar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ---------------------- LOGIN ----------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = loginRequest.get("email");
            String contrasena = loginRequest.get("contrasena");

            Usuario usuario = usuarioRepository.findByEmailAndContrasena(email, contrasena);

            if (usuario != null) {
                response.put("status", "success");
                response.put("message", "Bienvenido, " + usuario.getNombre());
                response.put("rol", usuario.getRol());
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Usuario o contraseña incorrectos.");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error en el login: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ---------------------- LISTAR TODOS LOS USUARIOS ----------------------
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // ---------------------- OBTENER USUARIO POR ID ----------------------
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable int id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ---------------------- ACTUALIZAR USUARIO ----------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable int id, @RequestBody Usuario usuarioDetalles) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setNombre(usuarioDetalles.getNombre());
            usuario.setEmail(usuarioDetalles.getEmail());
            usuario.setCelular(usuarioDetalles.getCelular());
            usuario.setRol(usuarioDetalles.getRol());
            usuario.setContrasena(usuarioDetalles.getContrasena());

            usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ---------------------- ELIMINAR USUARIO ----------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarUsuario(@PathVariable int id) {
        Map<String, String> response = new HashMap<>();
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            response.put("status", "ok");
            response.put("message", "Usuario eliminado correctamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Usuario no encontrado.");
            return ResponseEntity.status(404).body(response);
        }
    }
}
