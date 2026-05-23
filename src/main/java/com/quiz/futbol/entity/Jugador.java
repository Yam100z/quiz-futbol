package com.quiz.futbol.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "jugadores")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private int numero;
    private String posicion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_club")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "jugadores", "competiciones"})
    private Club club;

    public Jugador() {}

    public Jugador(Long id, String nombre, String apellido, int numero, String posicion, Club club) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numero = numero;
        this.posicion = posicion;
        this.club = club;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }
    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }

    @Override
    public String toString() {
        return "Jugador{id=" + id + ", nombre='" + nombre + "'}";
    }
}