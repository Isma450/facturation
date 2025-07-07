package com.myapp.facturation.service;

import java.time.YearMonth;

import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.model.Facture;
import com.myapp.facturation.model.Prestation;
import com.myapp.facturation.model.User;

public interface FactureService {
    Facture creerFactureMensuelle(Entreprise client, YearMonth mois);
    Facture creerFacturePourPrestation(Prestation prestation);
    Facture creerFacturePourPrestationAvecUser(Prestation prestation, User user);
    void exporterPdf(Facture facture);
}
