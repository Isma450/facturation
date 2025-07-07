package com.myapp.facturation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.model.Prestation;

@Repository
public interface PrestationRepository extends JpaRepository<Prestation, Long> {
    List<Prestation> findByClient(Entreprise client);
    List<Prestation> findByDateBetween(LocalDate start, LocalDate end);
}
