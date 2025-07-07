package com.myapp.facturation.model;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

import jakarta.persistence.Entity;

@Entity
public class Formation extends Prestation {

    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String classe;
    private String module;

    public Formation() {
        super();
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }
    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }
    public LocalTime getHeureFin() {
        return heureFin;
    }
    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }
    public String getClasse() {
        return classe;
    }
    public void setClasse(String classe) {
        this.classe = classe;
    }
    public String getModule() {
        return module;
    }
    public void setModule(String module) {
        this.module = module;
    }

    private static final BigDecimal TARIF_HORAIRE = new BigDecimal("75.00");

    public Formation(Long id, java.time.LocalDate date, Entreprise client,
                     LocalTime heureDebut, LocalTime heureFin,
                     String classe, String module) {
        setId(id);
        setDate(date);
        setClient(client);
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.classe = classe;
        this.module = module;
    }

    @Override
    public String getTypeName() {
        return "Formation";
    }

  @Override
public BigDecimal calculerMontant() {
    if (heureDebut == null || heureFin == null) return BigDecimal.ZERO;
    long minutes = Duration.between(heureDebut, heureFin).toMinutes();
    return TARIF_HORAIRE.multiply(BigDecimal.valueOf(minutes))
                        .divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
}
}
