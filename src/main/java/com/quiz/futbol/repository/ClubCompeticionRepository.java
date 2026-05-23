package com.quiz.futbol.repository;

import com.quiz.futbol.entity.ClubCompeticion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubCompeticionRepository extends JpaRepository<ClubCompeticion, Long> {
}