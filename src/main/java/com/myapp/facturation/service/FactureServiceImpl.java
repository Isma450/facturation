package com.myapp.facturation.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.model.Facture;
import com.myapp.facturation.model.Prestation;
import com.myapp.facturation.model.User;
import com.myapp.facturation.repository.FactureRepository;
import com.myapp.facturation.repository.PrestationRepository;

@Service
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final PrestationRepository prestationRepository;
    private final PdfGenerator pdfGenerator;

    public FactureServiceImpl(FactureRepository factureRepository, PrestationRepository prestationRepository, PdfGenerator pdfGenerator) {
        this.factureRepository = factureRepository;
        this.prestationRepository = prestationRepository;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public Facture creerFactureMensuelle(Entreprise client, YearMonth mois) {
        LocalDate debut = mois.atDay(1);
        LocalDate fin = mois.atEndOfMonth();

        List<Prestation> prestations = prestationRepository.findByDateBetween(debut, fin).stream()
                .filter(p -> p.getClient().getId().equals(client.getId()))
                .collect(Collectors.toList());

        Facture f = new Facture(null, client, debut, fin, prestations,
                "facture_" + client.getId() + "_" + mois + ".pdf", client.getId());

        return factureRepository.save(f);
    }

    @Override
    public Facture creerFacturePourPrestation(Prestation prestation) {
        Facture f = new Facture(
                null,
                prestation.getClient(),
                prestation.getDate(),
                prestation.getDate(),
                List.of(prestation),
                "facture_" + prestation.getId() + ".pdf",
                prestation.getUser().getId()
        );
        return factureRepository.save(f);
    }

    @Override
    public Facture creerFacturePourPrestationAvecUser(Prestation prestation, User user) {
        Facture f = new Facture(
                null,
                prestation.getClient(),
                prestation.getDate(),
                prestation.getDate(),
                List.of(prestation),
                "facture_" + prestation.getId() + ".pdf",
                user.getId()
        );
        return factureRepository.save(f);
    }

    @Override
    public void exporterPdf(Facture facture) {
        pdfGenerator.generer(facture, facture.getCheminPdf());
    }
}
