package com.myapp.facturation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BilanCA {
    private final LocalDate debut;
    private final LocalDate fin;
    private final Map<Entreprise, BigDecimal> caParClient = new HashMap<>();

    public BilanCA(LocalDate debut, LocalDate fin) {
        this.debut = debut;
        this.fin = fin;
    }

    public void ajouter(Entreprise client, BigDecimal montant) {
        caParClient.merge(client, montant, BigDecimal::add);
    }

    public Map<Entreprise, BigDecimal> getCaParClient() {
        return caParClient;
    }

    public BigDecimal getTotal() {
        return caParClient.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public LocalDate getDebut() {
        return debut;
    }

    public LocalDate getFin() {
        return fin;
    }
}
