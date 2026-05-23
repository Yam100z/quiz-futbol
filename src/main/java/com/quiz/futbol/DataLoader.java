package com.quiz.futbol;

import com.quiz.futbol.entity.*;
import com.quiz.futbol.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class DataLoader implements CommandLineRunner {

    private final AsociacionRepository asociacionRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final CompeticionRepository competicionRepository;
    private final ClubRepository clubRepository;
    private final JugadorRepository jugadorRepository;
    private final ClubCompeticionRepository clubCompeticionRepository;

    public DataLoader(AsociacionRepository asociacionRepository,
                      EntrenadorRepository entrenadorRepository,
                      CompeticionRepository competicionRepository,
                      ClubRepository clubRepository,
                      JugadorRepository jugadorRepository,
                      ClubCompeticionRepository clubCompeticionRepository) {
        this.asociacionRepository = asociacionRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.competicionRepository = competicionRepository;
        this.clubRepository = clubRepository;
        this.jugadorRepository = jugadorRepository;
        this.clubCompeticionRepository = clubCompeticionRepository;
    }

    @Override
    @jakarta.transaction.Transactional
    public void run(String... args) {

        // 1. Asociacion
        Asociacion fcf = new Asociacion();
        fcf.setNombre("Federación Colombiana de Fútbol");
        fcf.setPais("Colombia");
        fcf.setPresidente("Ramón Jesurún");
        asociacionRepository.save(fcf);

        // 2. Entrenadores
        Entrenador e1 = new Entrenador();
        e1.setNombre("Alberto");
        e1.setApellido("Gamero");
        e1.setEdad(57);
        e1.setNacionalidad("Colombiana");
        entrenadorRepository.save(e1);

        Entrenador e2 = new Entrenador();
        e2.setNombre("Osorio");
        e2.setApellido("Arroyave");
        e2.setEdad(62);
        e2.setNacionalidad("Colombiana");
        entrenadorRepository.save(e2);

        // 3. Competiciones
        Competicion copa = new Competicion();
        copa.setNombre("Copa Libertadores");
        copa.setMontoPremio(23000000);
        copa.setFechaInicio(LocalDate.of(2024, 2, 6));
        copa.setFechaFin(LocalDate.of(2024, 11, 30));
        competicionRepository.save(copa);

        Competicion liga = new Competicion();
        liga.setNombre("Liga BetPlay");
        liga.setMontoPremio(5000000);
        liga.setFechaInicio(LocalDate.of(2024, 1, 20));
        liga.setFechaFin(LocalDate.of(2024, 12, 15));
        competicionRepository.save(liga);

        // 4. Clubes
        Club millonarios = new Club();
        millonarios.setNombre("Millonarios FC");
        millonarios.setCiudad("Bogotá");
        millonarios.setAnioFundacion(1946);
        millonarios.setEntrenador(e1);
        millonarios.setAsociacion(fcf);
        millonarios.setJugadores(new ArrayList<>());
        clubRepository.save(millonarios);

        Club santafe = new Club();
        santafe.setNombre("Independiente Santa Fe");
        santafe.setCiudad("Bogotá");
        santafe.setAnioFundacion(1941);
        santafe.setEntrenador(e2);
        santafe.setAsociacion(fcf);
        santafe.setJugadores(new ArrayList<>());
        clubRepository.save(santafe);

        // 5. Jugadores
        Jugador j1 = new Jugador();
        j1.setNombre("David"); j1.setApellido("Mackalister");
        j1.setNumero(10); j1.setPosicion("Mediocampista");
        j1.setClub(millonarios);
        jugadorRepository.save(j1);

        Jugador j2 = new Jugador();
        j2.setNombre("Jader"); j2.setApellido("Valencia");
        j2.setNumero(9); j2.setPosicion("Delantero");
        j2.setClub(millonarios);
        jugadorRepository.save(j2);

        Jugador j3 = new Jugador();
        j3.setNombre("Wilson"); j3.setApellido("Morelo");
        j3.setNumero(11); j3.setPosicion("Delantero");
        j3.setClub(santafe);
        jugadorRepository.save(j3);

        // 6. ClubCompeticion (tabla intermedia como entidad)
        ClubCompeticion cc1 = new ClubCompeticion(millonarios, copa, LocalDate.of(2024, 1, 15), "ACTIVO");
        ClubCompeticion cc2 = new ClubCompeticion(millonarios, liga, LocalDate.of(2024, 1, 10), "ACTIVO");
        ClubCompeticion cc3 = new ClubCompeticion(santafe, copa, LocalDate.of(2024, 1, 15), "ELIMINADO");
        ClubCompeticion cc4 = new ClubCompeticion(santafe, liga, LocalDate.of(2024, 1, 10), "CAMPEON");
        clubCompeticionRepository.save(cc1);
        clubCompeticionRepository.save(cc2);
        clubCompeticionRepository.save(cc3);
        clubCompeticionRepository.save(cc4);

        System.out.println("✅ Datos de prueba cargados correctamente.");
        System.out.println("📌 H2 Console: http://localhost:8107/h2-console  |  JDBC URL: jdbc:h2:mem:futboldb");
        System.out.println("🔗 API: http://localhost:8107/api/clubes");
    }
}