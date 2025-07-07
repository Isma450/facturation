package com.myapp.facturation.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;

@Entity
public class Consultation extends Prestation {

    private BigDecimal tjm;

    public Consultation() {
        super();
    }

    public Consultation(Long id, java.time.LocalDate date, Entreprise client, BigDecimal tjm) {
        setId(id);
        setDate(date);
        setClient(client);
        this.tjm = tjm;
    }

    public BigDecimal getTjm() {
        return tjm;
    }

    public void setTjm(BigDecimal tjm) {
        this.tjm = tjm;
    }

    @Override
    public String getTypeName() {
        return "Consultation";
    }

    @Override
    public BigDecimal calculerMontant() {
        return tjm != null ? tjm : BigDecimal.ZERO;
    }
}
