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

        if (grafo.getAmigos(a).contains(b)) {
            throw new IllegalArgumentException("La amistad ya existe.");
        }

        //Grafo
        grafo.addAmistad(a, b);

        //sincronizar con Usuario
        Usuario u1 = usuarios.get(a);
        Usuario u2 = usuarios.get(b);

        u1.agregarAmigo(u2);
        u2.agregarAmigo(u1);
    }

    // Elimina una relación de amistad entre dos usuarios.
    public void removeFriend(String userA, String userB) {
        String a = norm(userA);
        String b = norm(userB);
        if (a == null || b == null) return;

        //Grafo
        grafo.removeAmistad(a, b);

        //sincronizar con Usuario
        Usuario u1 = usuarios.get(a);
        Usuario u2 = usuarios.get(b);

        if (u1 != null && u2 != null) {
            u1.eliminarAmigo(u2);
            u2.eliminarAmigo(u1);
        }
    }

    // Obtiene los amigos de un usuario.
    public Set<String> getFriends(String username) {
        String key = norm(username);
        if (key == null) return Set.of();
        return Collections.unmodifiableSet(new HashSet<>(grafo.getAmigos(key)));
    }

    // Genera sugerencias de amistad utilizando el algoritmo BFS (Búsqueda en Anchura).
    public Set<String> obtenerSugerenciasBFS(String username) {

        // Normalizamos el username
        String usuarioInicial = norm(username);

        // Validamos que el usuario exista
        if (usuarioInicial == null || !usuarios.containsKey(usuarioInicial)) {
            return Set.of();
        }

        // Conjunto donde se guardarán las sugerencias
        Set<String> sugerencias = new HashSet<>();

        // Obtenemos los amigos directos (para excluirlos)
        Set<String> amigosDirectos = getFriends(usuarioInicial);

        // Cola para el recorrido BFS
        Queue<String> cola = new LinkedList<>();

        // Conjunto para marcar los usuarios visitados
        Set<String> visitados = new HashSet<>();

        // Iniciamos el recorrido desde el usuario
        cola.add(usuarioInicial);
        visitados.add(usuarioInicial);

        // Mientras haya usuarios en la cola
        while (!cola.isEmpty()) {

            // Sacamos el siguiente usuario
            String actual = cola.poll();

            // Recorremos sus amigos
            for (String amigo : grafo.getAmigos(actual)) {

                // Si no ha sido visitado
                if (!visitados.contains(amigo)) {

                    // Lo marcamos como visitado y lo agregamos a la cola
                    visitados.add(amigo);
                    cola.add(amigo);

                    // Aplicamos reglas de filtrado
                    if (!amigo.equals(usuarioInicial) &&
                            !amigosDirectos.contains(amigo)) {

                        sugerencias.add(amigo);
                    }
                }
            }
        }

        // Retornamos las sugerencias sin duplicados
        return sugerencias;
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
    // Asigna un grupo a un usuario
    public void asignarGrupo(String username, String nombreGrupo) {
        String key = norm(username);

        if (key == null || !usuarios.containsKey(key)) {
            throw new IllegalArgumentException("Usuario no existe.");
        }

        Usuario usuario = usuarios.get(key);

        Grupo grupoEncontrado = grupos.stream()
                .filter(g -> g.getNombre().equalsIgnoreCase(nombreGrupo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Grupo no existe."));

        usuario.setGrupo(grupoEncontrado);
    }
    // Retorna la lista de grupos registrados
    public List<Grupo> getGrupos() {
        // Usamos unmodifiableList para evitar que otra parte del programa haga: service.getGrupos().clear(); y borre todos los grupos.
        return Collections.unmodifiableList(grupos);
    }
    // Obtener usuario con más amigos
    public Usuario obtenerUsuarioConMasAmigos() {
        if (usuarios.isEmpty()) return null;

        Usuario mayor = null;

        for (Usuario u : usuarios.values()) {
            if (mayor == null || u.getCantidadAmigos() > mayor.getCantidadAmigos()) {
                mayor = u;
            }
        }

        return mayor;
    }

    // Funcionalidad extra propia del proyecto
    // Amigos en Común
    public Set<String> amigosEnComun(String userA, String userB) {

        String a = norm(userA);
        String b = norm(userB);

        if (a == null || b == null) return Set.of();

        if (!usuarios.containsKey(a) || !usuarios.containsKey(b)) {
            throw new IllegalArgumentException("Usuario no existe.");
        }

        // Obtener amigos de ambos
        Set<String> amigosA = grafo.getAmigos(a);
        Set<String> amigosB = grafo.getAmigos(b);

        // Intersección
        Set<String> comunes = new HashSet<>(amigosA);
        comunes.retainAll(amigosB);

        return comunes;
    }

}