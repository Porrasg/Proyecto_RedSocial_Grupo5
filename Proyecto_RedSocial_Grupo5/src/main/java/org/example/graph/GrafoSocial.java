package org.example.graph;

import org.example.model.Usuario;

import java.util.*;

// Clase que representa un grafo social.
// Utiliza una estructura de lista de adyacencia donde:
// - La clave (String) es el username del usuario
// - El valor (Set<String>) es el conjunto de usernames de sus amigos
// Este grafo es no dirigido, es decir: si A es amigo de B, entonces B también es amigo de A.
public class GrafoSocial {

    // lista de adyacencia
    private final Map<String, Set<String>> relaciones = new HashMap<>();

    // Normaliza un username para evitar inconsistencias.
    private String norm(String username) {
        if (username == null) return null;
        return username.trim().toLowerCase();
    }

    // Agrega un usuario al grafo. Si el usuario ya existe, no se vuelve a agregar.
    public void addUsuario(Usuario usuario) {
        String key = norm(usuario.getUsername());
        relaciones.putIfAbsent(key, new HashSet<>());
    }

    // Crea una relación de amistad entre dos usuarios.
    // Como el grafo es no dirigido, se agrega en ambos sentidos: A → B y B → A
    // Si los usuarios no existen en el grafo, se crean automáticamente.
    public void addAmistad(String userA, String userB) {
        String a = norm(userA);
        String b = norm(userB);

        relaciones.putIfAbsent(a, new HashSet<>());
        relaciones.putIfAbsent(b, new HashSet<>());

        relaciones.get(a).add(b);
        relaciones.get(b).add(a);
    }

    // Elimina la relación de amistad entre dos usuarios. Si alguno de los usuarios no existe, no se produce error.
    public void removeAmistad(String userA, String userB) {
        String a = norm(userA);
        String b = norm(userB);

        relaciones.getOrDefault(a, new HashSet<>()).remove(b);
        relaciones.getOrDefault(b, new HashSet<>()).remove(a);
    }

    // Obtiene la lista de amigos de un usuario. Si el usuario no existe, retorna un conjunto vacío.
    public Set<String> getAmigos(String username) {
        String key = norm(username);
        return Collections.unmodifiableSet(relaciones.getOrDefault(key, Collections.emptySet()));
    }

    // Retorna todas las relaciones del grafo.
    public Map<String, Set<String>> getRelaciones() {
        return relaciones;
    }
}