package com.myapp.facturation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.model.Facture;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    
    @Query("SELECT f FROM Facture f WHERE f.client.id = :clientId ORDER BY f.id DESC")
    List<Facture> findByClientIdOrderByIdDesc(@Param("clientId") Long clientId);
    
    @Query("SELECT f FROM Facture f JOIN FETCH f.prestations WHERE f.client = :client ORDER BY f.id DESC")
    List<Facture> findByClientWithPrestationsOrderByIdDesc(@Param("client") Entreprise client);
}
