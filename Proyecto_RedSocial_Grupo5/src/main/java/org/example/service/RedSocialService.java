package org.example.service;

import org.example.graph.GrafoSocial;
import org.example.model.Usuario;

import java.util.*;

public class RedSocialService {

    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final GrafoSocial grafo = new GrafoSocial();

    private String norm(String username) {
        if (username == null) return null;
        return username.trim().toLowerCase();
    }

    public boolean existsUsername(String username) {
        String key = norm(username);
        return key != null && usuarios.containsKey(key);
    }

    public void addUser(Usuario usuario) {
        String key = norm(usuario.getUsername());
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("El usuario es obligatorio.");
        }
        if (usuarios.containsKey(key)) {
            throw new IllegalArgumentException("Ese usuario ya existe.");
        }

        usuarios.put(key, usuario);
        grafo.addUsuario(usuario);
    }

    // Para Integrante 2
    public void addFriend(String userA, String userB) {
        String a = norm(userA);
        String b = norm(userB);

        if (a == null || b == null || a.isBlank() || b.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar dos usuarios válidos.");
        }
        if (a.equals(b)) {
            throw new IllegalArgumentException("No puedes agregarte a ti mismo.");
        }
        if (!usuarios.containsKey(a) || !usuarios.containsKey(b)) {
            throw new IllegalArgumentException("Usuario no existe.");
        }

        //  Esto evita duplicados
        if (grafo.getAmigos(a).contains(b)) {
            throw new IllegalArgumentException("La amistad ya existe.");
        }

        grafo.addAmistad(a, b);
    }

    // (Opcional para Integrante 2)
    public void removeFriend(String userA, String userB) {
        String a = norm(userA);
        String b = norm(userB);
        if (a == null || b == null) return;
        grafo.removeAmistad(a, b);
    }

    // Para Integrante 3
    public Set<String> getFriends(String username) {
        String key = norm(username);
        if (key == null) return Set.of();
        return Collections.unmodifiableSet(new HashSet<>(grafo.getAmigos(key)));
    }

    public List<Usuario> getAllUsers() {
        return usuarios.values().stream()
                .sorted(Comparator.comparing(Usuario::getUsername, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Optional<Usuario> findByUsername(String username) {
        String key = norm(username);
        if (key == null) return Optional.empty();
        return Optional.ofNullable(usuarios.get(key));
    }
}