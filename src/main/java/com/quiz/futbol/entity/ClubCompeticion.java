package com.quiz.futbol.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "club_competicion")
public class ClubCompeticion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fechaInscripcion;
    private String estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "jugadores", "competiciones"})
    private Club club;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "competicion_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "clubes"})
    private Competicion competicion;

    public ClubCompeticion() {}

    public ClubCompeticion(Club club, Competicion competicion,
                           LocalDate fechaInscripcion, String estado) {
        this.club = club;
        this.competicion = competicion;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }
    public Competicion getCompeticion() { return competicion; }
    public void setCompeticion(Competicion competicion) { this.competicion = competicion; }

    @Override
    public String toString() {
        return "ClubCompeticion{id=" + id + ", estado='" + estado + "'}";
    }
}