package com.myapp.facturation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.model.Prestation;
import com.myapp.facturation.repository.PrestationRepository;


@Service
public class PrestationServiceImpl implements PrestationService {

    private final PrestationRepository prestationRepository;

    public PrestationServiceImpl(PrestationRepository prestationRepository) {
        this.prestationRepository = prestationRepository;
    }

    @Override
    public List<Prestation> findByClient(Entreprise client) {
        return prestationRepository.findByClient(client);
    }

    @Override
    public Prestation findById(Long id) {
        return prestationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prestation non trouvée avec l'id: " + id));
    }
}
