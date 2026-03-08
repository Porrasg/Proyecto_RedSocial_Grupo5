package org.example.model;

import java.time.LocalDate;
import java.util.Objects;
import org.example.model.Grupo;

public class Usuario {
    // Datos básicos del usuario
    private final String username;
    private final String password;
    private final String primerNombre;
    private final String primerApellido;
    private final String segundoApellido;
    private final LocalDate fechaNacimiento;
    // Tipo de avatar seleccionado
    private final AvatarType avatarType;
    // Grupo al que pertenece el usuario (puede asignarse después)
    private Grupo grupo;

    // Constructor para crear un nuevo usuario
    public Usuario(String username,
                   String password,
                   String primerNombre,
                   String primerApellido,
                   String segundoApellido,
                   LocalDate fechaNacimiento,
                   AvatarType avatarType) {
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.primerNombre = Objects.requireNonNull(primerNombre);
        this.primerApellido = Objects.requireNonNull(primerApellido);
        this.segundoApellido = Objects.requireNonNull(segundoApellido);
        this.fechaNacimiento = Objects.requireNonNull(fechaNacimiento);
        this.avatarType = Objects.requireNonNull(avatarType);
    }

    // Getters para acceder a los datos del usuario
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getPrimerNombre() { return primerNombre; }
    public String getPrimerApellido() { return primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public AvatarType getAvatarType() { return avatarType; }
    public Grupo getGrupo() { return grupo; } // Obtener el grupo del usuario


    // Asignar o cambiar el grupo del usuario
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
}