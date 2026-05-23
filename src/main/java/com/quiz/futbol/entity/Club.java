package com.quiz.futbol.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "clubes")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String ciudad;
    private int anioFundacion;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entrenador_id", nullable = true, unique = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Entrenador entrenador;

    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Jugador> jugadores;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asociacion_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "clubes"})
    private Asociacion asociacion;

    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ClubCompeticion> competiciones;

    public Club() {}

    public Club(Long id, String nombre, String ciudad, int anioFundacion,
                Entrenador entrenador, List<Jugador> jugadores,
                Asociacion asociacion, List<ClubCompeticion> competiciones) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.anioFundacion = anioFundacion;
        this.entrenador = entrenador;
        this.jugadores = jugadores;
        this.asociacion = asociacion;
        this.competiciones = competiciones;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public int getAnioFundacion() { return anioFundacion; }
    public void setAnioFundacion(int anioFundacion) { this.anioFundacion = anioFundacion; }
    public Entrenador getEntrenador() { return entrenador; }
    public void setEntrenador(Entrenador entrenador) { this.entrenador = entrenador; }
    public List<Jugador> getJugadores() { return jugadores; }
    public void setJugadores(List<Jugador> jugadores) { this.jugadores = jugadores; }
    public Asociacion getAsociacion() { return asociacion; }
    public void setAsociacion(Asociacion asociacion) { this.asociacion = asociacion; }
    public List<ClubCompeticion> getCompeticiones() { return competiciones; }
    public void setCompeticiones(List<ClubCompeticion> competiciones) { this.competiciones = competiciones; }

    @Override
    public String toString() {
        return "Club{id=" + id + ", nombre='" + nombre + "'}";
    }
}