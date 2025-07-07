package com.myapp.facturation.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myapp.facturation.model.BilanCA;
import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.model.Prestation;
import com.myapp.facturation.repository.PrestationRepository;

@Service
public class BilanServiceImpl implements BilanService {

    private final PrestationRepository prestationRepository;

    public BilanServiceImpl(PrestationRepository prestationRepository) {
        this.prestationRepository = prestationRepository;
    }

    @Override
    public BilanCA calculerBilanMensuel(Entreprise client, YearMonth mois) {
        LocalDate debut = mois.atDay(1);
        LocalDate fin = mois.atEndOfMonth();
        return calculerBilanPeriode(client, debut, fin);
    }

    @Override
    public BilanCA calculerBilanAnnuel(Entreprise client, int annee) {
        LocalDate debut = LocalDate.of(annee, 1, 1);
        LocalDate fin = LocalDate.of(annee, 12, 31);
        return calculerBilanPeriode(client, debut, fin);
    }

    @Override
    public BilanCA calculerBilanPeriode(Entreprise client, LocalDate debut, LocalDate fin) {
        List<Prestation> prestations = prestationRepository.findByDateBetween(debut, fin);
        BilanCA bilan = new BilanCA(debut, fin);

        for (Prestation p : prestations) {
            if (client == null || p.getClient().getId().equals(client.getId())) {
                bilan.ajouter(p.getClient(), p.calculerMontant());
            }
        }

        return bilan;
    }
}
