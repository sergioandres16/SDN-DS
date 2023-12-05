package com.example.dataservice.Entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "usuario")  // Use @Table to specify the table name
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El campo nombres es obligatorio")
    private String nombres;

    @NotNull(message = "El campo apellidos es obligatorio")
    private String apellidos;

    private String facultad;

    private String especialidad;

    private Integer id_rol;
    @NotNull(message = "El username es obligatorio")
    private String username;

    public User(Integer id, String nombres, String apellidos, String facultad, String especialidad, Integer id_rol, String username, String password) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.facultad = facultad;
        this.especialidad = especialidad;
        this.id_rol = id_rol;
        this.username = username;
        this.password = password;
    }

    @NotNull(message = "La contraseña es obligatoria")
    private String password; // sha256

    public User() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Integer getId_rol() {
        return id_rol;
    }

    public void setId_rol(Integer id_rol) {
        this.id_rol = id_rol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}