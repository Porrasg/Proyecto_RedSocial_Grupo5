package org.example.service;

import org.example.graph.GrafoSocial;
import org.example.model.Usuario;
import org.example.model.Grupo;

import java.util.*;
// Clase de servicio que gestiona la lógica principal de la red social.
// Se encarga de:
// - Registrar usuarios
// - Gestionar amistades (a través del grafo)
// - Consultar usuarios y relaciones
// - Manejar los grupos
public class RedSocialService {

    // Mapa de usuarios registrados (clave: username normalizado)
    private final Map<String, Usuario> usuarios = new HashMap<>();

    //Grafo que almacena las relaciones de amistad
    private final GrafoSocial grafo = new GrafoSocial();

    // Lista donde se almacenan los grupos creados en la red social
    private final List<Grupo> grupos = new ArrayList<>();

    // Normaliza un username (minúsculas y sin espacios).
    private String norm(String username) {
        if (username == null) return null;
        return username.trim().toLowerCase();
    }

    // Verifica si un username ya existe en el sistema.
    public boolean existsUsername(String username) {
        String key = norm(username);
        return key != null && usuarios.containsKey(key);
    }

    // Agrega un nuevo usuario al sistema. También lo registra dentro del grafo social.
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

    // Crea una relación de amistad entre dos usuarios.
    // Valida: - que los usuarios existan - que no sean el mismo usuario - que la amistad no exista previamente
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

    // Elimina una relación de amistad entre dos usuarios.
    public void removeFriend(String userA, String userB) {
        String a = norm(userA);
        String b = norm(userB);
        if (a == null || b == null) return;
        grafo.removeAmistad(a, b);
    }

    // Obtiene los amigos de un usuario.
    public Set<Object> getFriends(String username) {
        String key = norm(username);
        if (key == null) return Set.of();
        return Collections.unmodifiableSet(new HashSet<>(grafo.getAmigos(key)));
    }

    // Retorna todos los usuarios registrados ordenados alfabéticamente.
    public List<Usuario> getAllUsers() {
        return usuarios.values().stream()
                .sorted(Comparator.comparing(Usuario::getUsername, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    // Busca un usuario por su username.
    public Optional<Usuario> findByUsername(String username) {
        String key = norm(username);
        if (key == null) return Optional.empty();
        return Optional.ofNullable(usuarios.get(key));
    }

    // Permite crear un nuevo grupo
    public void agregarGrupo(Grupo grupo) {
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo no puede ser nulo.");
        }
        grupos.add(grupo);
    }

    // Retorna la lista de grupos registrados
    public List<Grupo> getGrupos() {
        // Usamos unmodifiableList para evitar que otra parte del programa haga: service.getGrupos().clear(); y borre todos los grupos.
        return Collections.unmodifiableList(grupos);
    }


}