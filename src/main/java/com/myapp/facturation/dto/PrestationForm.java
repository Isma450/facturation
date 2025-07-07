package com.myapp.facturation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;


/**
 * DTO pour la création ou la mise à jour d'une Prestation.
 * Utilisé dans les formulaires de création et de modification.
 */
public class PrestationForm {

    private String type; 
    private Long clientId;
    private LocalDate date;
    
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String classe;
    private String module;
    
    private BigDecimal tjm;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Long getClientId() {
        return clientId;
    }
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
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
    public BigDecimal getTjm() {
        return tjm;
    }
    public void setTjm(BigDecimal tjm) {
        this.tjm = tjm;
    }
}
