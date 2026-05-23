package com.quiz.futbol.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "asociaciones")
public class Asociacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String pais;
    private String presidente;

    @OneToMany(mappedBy = "asociacion", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Club> clubes;

    public Asociacion() {}

    public Asociacion(Long id, String nombre, String pais, String presidente, List<Club> clubes) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.presidente = presidente;
        this.clubes = clubes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getPresidente() { return presidente; }
    public void setPresidente(String presidente) { this.presidente = presidente; }
    public List<Club> getClubes() { return clubes; }
    public void setClubes(List<Club> clubes) { this.clubes = clubes; }

    @Override
    public String toString() {
        return "Asociacion{id=" + id + ", nombre='" + nombre + "'}";
    }
}