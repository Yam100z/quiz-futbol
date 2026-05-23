package com.quiz.futbol.controller;

import com.quiz.futbol.entity.*;
import com.quiz.futbol.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class FutbolController {

    private final ClubRepository clubRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final JugadorRepository jugadorRepository;
    private final AsociacionRepository asociacionRepository;
    private final CompeticionRepository competicionRepository;
    private final ClubCompeticionRepository clubCompeticionRepository;

    public FutbolController(ClubRepository clubRepository,
                            EntrenadorRepository entrenadorRepository,
                            JugadorRepository jugadorRepository,
                            AsociacionRepository asociacionRepository,
                            CompeticionRepository competicionRepository,
                            ClubCompeticionRepository clubCompeticionRepository) {
        this.clubRepository = clubRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.jugadorRepository = jugadorRepository;
        this.asociacionRepository = asociacionRepository;
        this.competicionRepository = competicionRepository;
        this.clubCompeticionRepository = clubCompeticionRepository;
    }

    // ==================== CLUBES ====================
    @GetMapping("/api/clubes")
    public List<Club> getAllClubes() {
        return clubRepository.findAll();
    }

    @GetMapping("/api/clubes/{id}")
    public ResponseEntity<Club> getClub(@PathVariable Long id) {
        return clubRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/clubes")
    public ResponseEntity<Club> saveClub(@RequestBody java.util.Map<String, Object> body) {
        Club club = new Club();
        club.setNombre((String) body.get("nombre"));
        club.setCiudad((String) body.get("ciudad"));
        Object anio = body.get("anioFundacion");
        if (anio != null) club.setAnioFundacion(((Number) anio).intValue());

        // @OneToOne Entrenador
        if (body.get("entrenador") instanceof java.util.Map) {
            java.util.Map<?,?> ent = (java.util.Map<?,?>) body.get("entrenador");
            if (ent.get("id") != null) {
                entrenadorRepository.findById(((Number) ent.get("id")).longValue())
                        .ifPresent(club::setEntrenador);
            }
        }

        // @ManyToOne Asociacion
        if (body.get("asociacion") instanceof java.util.Map) {
            java.util.Map<?,?> aso = (java.util.Map<?,?>) body.get("asociacion");
            if (aso.get("id") != null) {
                asociacionRepository.findById(((Number) aso.get("id")).longValue())
                        .ifPresent(club::setAsociacion);
            }
        }

        club.setCompeticiones(new ArrayList<>());
        Club savedClub = clubRepository.save(club);

        // @OneToMany Jugadores — asignar jugadores al club
        if (body.get("jugadoresSeleccionados") instanceof java.util.List) {
            java.util.List<?> ids = (java.util.List<?>) body.get("jugadoresSeleccionados");
            for (Object idObj : ids) {
                Long jugadorId = ((Number) idObj).longValue();
                jugadorRepository.findById(jugadorId).ifPresent(j -> {
                    j.setClub(savedClub);
                    jugadorRepository.save(j);
                });
            }
        }

        // @ManyToMany Competiciones — crear ClubCompeticion
        if (body.get("competicionesSeleccionadas") instanceof java.util.List) {
            java.util.List<?> ids = (java.util.List<?>) body.get("competicionesSeleccionadas");
            for (Object idObj : ids) {
                Long compId = ((Number) idObj).longValue();
                competicionRepository.findById(compId).ifPresent(comp -> {
                    ClubCompeticion cc = new ClubCompeticion();
                    cc.setClub(savedClub);
                    cc.setCompeticion(comp);
                    cc.setEstado("ACTIVO");
                    cc.setFechaInscripcion(java.time.LocalDate.now());
                    clubCompeticionRepository.save(cc);
                });
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(clubRepository.findById(savedClub.getId()).orElse(savedClub));
    }

    @PutMapping("/api/clubes/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable Long id, @RequestBody java.util.Map<String, Object> body) {
        return clubRepository.findById(id).map(c -> {
            c.setNombre((String) body.get("nombre"));
            c.setCiudad((String) body.get("ciudad"));
            Object anio = body.get("anioFundacion");
            if (anio != null) c.setAnioFundacion(((Number) anio).intValue());

            // @OneToOne Entrenador
            if (body.get("entrenador") instanceof java.util.Map) {
                java.util.Map<?,?> ent = (java.util.Map<?,?>) body.get("entrenador");
                if (ent.get("id") != null) {
                    entrenadorRepository.findById(((Number) ent.get("id")).longValue())
                            .ifPresent(c::setEntrenador);
                } else { c.setEntrenador(null); }
            } else { c.setEntrenador(null); }

            // @ManyToOne Asociacion
            if (body.get("asociacion") instanceof java.util.Map) {
                java.util.Map<?,?> aso = (java.util.Map<?,?>) body.get("asociacion");
                if (aso.get("id") != null) {
                    asociacionRepository.findById(((Number) aso.get("id")).longValue())
                            .ifPresent(c::setAsociacion);
                } else { c.setAsociacion(null); }
            } else { c.setAsociacion(null); }

            Club savedClub = clubRepository.save(c);

            // @OneToMany Jugadores — desasignar anteriores y asignar nuevos
            if (body.get("jugadoresSeleccionados") instanceof java.util.List) {
                // Desasignar jugadores anteriores de este club
                jugadorRepository.findAll().stream()
                    .filter(j -> j.getClub() != null && j.getClub().getId().equals(id))
                    .forEach(j -> { j.setClub(null); jugadorRepository.save(j); });
                // Asignar nuevos
                java.util.List<?> ids = (java.util.List<?>) body.get("jugadoresSeleccionados");
                for (Object idObj : ids) {
                    Long jugadorId = ((Number) idObj).longValue();
                    jugadorRepository.findById(jugadorId).ifPresent(j -> {
                        j.setClub(savedClub);
                        jugadorRepository.save(j);
                    });
                }
            }

            // @ManyToMany Competiciones — eliminar anteriores y crear nuevas
            if (body.get("competicionesSeleccionadas") instanceof java.util.List) {
                // Eliminar inscripciones anteriores de este club
                clubCompeticionRepository.findAll().stream()
                    .filter(cc -> cc.getClub() != null && cc.getClub().getId().equals(id))
                    .forEach(clubCompeticionRepository::delete);
                // Crear nuevas
                java.util.List<?> ids = (java.util.List<?>) body.get("competicionesSeleccionadas");
                for (Object idObj : ids) {
                    Long compId = ((Number) idObj).longValue();
                    competicionRepository.findById(compId).ifPresent(comp -> {
                        ClubCompeticion cc = new ClubCompeticion();
                        cc.setClub(savedClub);
                        cc.setCompeticion(comp);
                        cc.setEstado("ACTIVO");
                        cc.setFechaInscripcion(java.time.LocalDate.now());
                        clubCompeticionRepository.save(cc);
                    });
                }
            }

            return ResponseEntity.ok(clubRepository.findById(savedClub.getId()).orElse(savedClub));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/clubes/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        if (clubRepository.existsById(id)) {
            clubRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== ENTRENADORES ====================
    @GetMapping("/api/entrenadores")
    public List<Entrenador> getAllEntrenadores() {
        return entrenadorRepository.findAll();
    }

    // IMPORTANTE: siempre ANTES de /{id} para que Spring no lo confunda
    @GetMapping("/api/entrenadores/disponibles")
    public List<Entrenador> getEntrenadoresDisponibles() {
        // Devuelve TODOS los entrenadores
        // El frontend marca como disabled los ya asignados
        return entrenadorRepository.findAll();
    }

    @GetMapping("/api/entrenadores/{id}")
    public ResponseEntity<Entrenador> getEntrenador(@PathVariable Long id) {
        return entrenadorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/entrenadores")
    public ResponseEntity<Entrenador> saveEntrenador(@RequestBody Entrenador e) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entrenadorRepository.save(e));
    }

    @PutMapping("/api/entrenadores/{id}")
    public ResponseEntity<Entrenador> updateEntrenador(@PathVariable Long id, @RequestBody Entrenador datos) {
        return entrenadorRepository.findById(id).map(e -> {
            e.setNombre(datos.getNombre());
            e.setApellido(datos.getApellido());
            e.setEdad(datos.getEdad());
            e.setNacionalidad(datos.getNacionalidad());
            return ResponseEntity.ok(entrenadorRepository.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/entrenadores/{id}")
    public ResponseEntity<Void> deleteEntrenador(@PathVariable Long id) {
        if (entrenadorRepository.existsById(id)) {
            entrenadorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== JUGADORES ====================
    @GetMapping("/api/jugadores")
    public List<Jugador> getAllJugadores() {
        return jugadorRepository.findAll();
    }

    @GetMapping("/api/jugadores/{id}")
    public ResponseEntity<Jugador> getJugador(@PathVariable Long id) {
        return jugadorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/jugadores")
    public ResponseEntity<Jugador> saveJugador(@RequestBody Jugador jugador) {
        if (jugador.getClub() != null && jugador.getClub().getId() != null) {
            clubRepository.findById(jugador.getClub().getId())
                    .ifPresent(jugador::setClub);
        } else {
            jugador.setClub(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(jugadorRepository.save(jugador));
    }

    @PutMapping("/api/jugadores/{id}")
    public ResponseEntity<Jugador> updateJugador(@PathVariable Long id, @RequestBody Jugador datos) {
        return jugadorRepository.findById(id).map(j -> {
            j.setNombre(datos.getNombre());
            j.setApellido(datos.getApellido());
            j.setNumero(datos.getNumero());
            j.setPosicion(datos.getPosicion());
            if (datos.getClub() != null && datos.getClub().getId() != null) {
                clubRepository.findById(datos.getClub().getId())
                        .ifPresent(j::setClub);
            } else {
                j.setClub(null);
            }
            return ResponseEntity.ok(jugadorRepository.save(j));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/jugadores/{id}")
    public ResponseEntity<Void> deleteJugador(@PathVariable Long id) {
        if (jugadorRepository.existsById(id)) {
            jugadorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== ASOCIACIONES ====================
    @GetMapping("/api/asociaciones")
    public List<Asociacion> getAllAsociaciones() {
        return asociacionRepository.findAll();
    }

    @GetMapping("/api/asociaciones/{id}")
    public ResponseEntity<Asociacion> getAsociacion(@PathVariable Long id) {
        return asociacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/asociaciones")
    public ResponseEntity<Asociacion> saveAsociacion(@RequestBody Asociacion a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asociacionRepository.save(a));
    }

    @PutMapping("/api/asociaciones/{id}")
    public ResponseEntity<Asociacion> updateAsociacion(@PathVariable Long id, @RequestBody Asociacion datos) {
        return asociacionRepository.findById(id).map(a -> {
            a.setNombre(datos.getNombre());
            a.setPais(datos.getPais());
            a.setPresidente(datos.getPresidente());
            return ResponseEntity.ok(asociacionRepository.save(a));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/asociaciones/{id}")
    public ResponseEntity<Void> deleteAsociacion(@PathVariable Long id) {
        if (asociacionRepository.existsById(id)) {
            asociacionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== COMPETICIONES ====================
    @GetMapping("/api/competiciones")
    public List<Competicion> getAllCompeticiones() {
        return competicionRepository.findAll();
    }

    @GetMapping("/api/competiciones/{id}")
    public ResponseEntity<Competicion> getCompeticion(@PathVariable Long id) {
        return competicionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/competiciones")
    public ResponseEntity<Competicion> saveCompeticion(@RequestBody Competicion c) {
        return ResponseEntity.status(HttpStatus.CREATED).body(competicionRepository.save(c));
    }

    @PutMapping("/api/competiciones/{id}")
    public ResponseEntity<Competicion> updateCompeticion(@PathVariable Long id, @RequestBody Competicion datos) {
        return competicionRepository.findById(id).map(c -> {
            c.setNombre(datos.getNombre());
            c.setMontoPremio(datos.getMontoPremio());
            c.setFechaInicio(datos.getFechaInicio());
            c.setFechaFin(datos.getFechaFin());
            return ResponseEntity.ok(competicionRepository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/competiciones/{id}")
    public ResponseEntity<Void> deleteCompeticion(@PathVariable Long id) {
        if (competicionRepository.existsById(id)) {
            competicionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== CLUB COMPETICION ====================
    @GetMapping("/api/club-competicion")
    public List<ClubCompeticion> getAllClubCompeticion() {
        return clubCompeticionRepository.findAll();
    }

    @GetMapping("/api/club-competicion/{id}")
    public ResponseEntity<ClubCompeticion> getClubCompeticion(@PathVariable Long id) {
        return clubCompeticionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/club-competicion")
    public ResponseEntity<ClubCompeticion> saveClubCompeticion(@RequestBody ClubCompeticion cc) {
        if (cc.getClub() != null && cc.getClub().getId() != null) {
            clubRepository.findById(cc.getClub().getId())
                    .ifPresent(cc::setClub);
        }
        if (cc.getCompeticion() != null && cc.getCompeticion().getId() != null) {
            competicionRepository.findById(cc.getCompeticion().getId())
                    .ifPresent(cc::setCompeticion);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(clubCompeticionRepository.save(cc));
    }

    @PutMapping("/api/club-competicion/{id}")
    public ResponseEntity<ClubCompeticion> updateClubCompeticion(@PathVariable Long id,
                                                                  @RequestBody ClubCompeticion datos) {
        return clubCompeticionRepository.findById(id).map(cc -> {
            cc.setEstado(datos.getEstado());
            cc.setFechaInscripcion(datos.getFechaInscripcion());
            if (datos.getClub() != null && datos.getClub().getId() != null) {
                clubRepository.findById(datos.getClub().getId())
                        .ifPresent(cc::setClub);
            }
            if (datos.getCompeticion() != null && datos.getCompeticion().getId() != null) {
                competicionRepository.findById(datos.getCompeticion().getId())
                        .ifPresent(cc::setCompeticion);
            }
            return ResponseEntity.ok(clubCompeticionRepository.save(cc));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/club-competicion/{id}")
    public ResponseEntity<Void> deleteClubCompeticion(@PathVariable Long id) {
        if (clubCompeticionRepository.existsById(id)) {
            clubCompeticionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}