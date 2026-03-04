package org.example.graph;

import org.example.model.Usuario;

import java.util.*;

public class GrafoSocial {

    // lista de adyacencia
    private final Map<String, Set<String>> relaciones = new HashMap<>();

    private String norm(String username) {
        if (username == null) return null;
        return username.trim().toLowerCase();
    }

    public void addUsuario(Usuario usuario) {
        String key = norm(usuario.getUsername());
        relaciones.putIfAbsent(key, new HashSet<>());
    }

    public void addAmistad(String userA, String userB) {
        String a = norm(userA);
        String b = norm(userB);

        relaciones.putIfAbsent(a, new HashSet<>());
        relaciones.putIfAbsent(b, new HashSet<>());

        relaciones.get(a).add(b);
        relaciones.get(b).add(a);
    }

    public void removeAmistad(String userA, String userB) {
        String a = norm(userA);
        String b = norm(userB);

        relaciones.getOrDefault(a, new HashSet<>()).remove(b);
        relaciones.getOrDefault(b, new HashSet<>()).remove(a);
    }

    public Set<String> getAmigos(String username) {
        String key = norm(username);
        return relaciones.getOrDefault(key, new HashSet<>());
    }

    public Map<String, Set<String>> getRelaciones() {
        return relaciones;
    }
}