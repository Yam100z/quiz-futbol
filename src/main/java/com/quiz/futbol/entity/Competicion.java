package com.quiz.futbol.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "competiciones")
public class Competicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private int montoPremio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @OneToMany(mappedBy = "competicion", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ClubCompeticion> clubes;

    public Competicion() {}

    public Competicion(Long id, String nombre, int montoPremio,
                       LocalDate fechaInicio, LocalDate fechaFin, List<ClubCompeticion> clubes) {
        this.id = id;
        this.nombre = nombre;
        this.montoPremio = montoPremio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.clubes = clubes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getMontoPremio() { return montoPremio; }
    public void setMontoPremio(int montoPremio) { this.montoPremio = montoPremio; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public List<ClubCompeticion> getClubes() { return clubes; }
    public void setClubes(List<ClubCompeticion> clubes) { this.clubes = clubes; }

    @Override
    public String toString() {
        return "Competicion{id=" + id + ", nombre='" + nombre + "'}";
    }
}