package com.myapp.facturation.service;

import com.myapp.facturation.model.BilanCA;
import com.myapp.facturation.model.Entreprise;

import java.time.LocalDate;
import java.time.YearMonth;

public interface BilanService {
    BilanCA calculerBilanMensuel(Entreprise client, YearMonth mois);
    BilanCA calculerBilanAnnuel(Entreprise client, int annee);
    BilanCA calculerBilanPeriode(Entreprise client, LocalDate debut, LocalDate fin);
}
