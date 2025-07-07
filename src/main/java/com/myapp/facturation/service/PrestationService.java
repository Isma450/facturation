package com.myapp.facturation.service;

import java.util.List;

import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.model.Prestation;

public interface PrestationService {
    List<Prestation> findByClient(Entreprise client);
    Prestation findById(Long id);
}
