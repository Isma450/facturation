package com.myapp.facturation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Entreprise client;

    private LocalDate periodeDebut;
    private LocalDate periodeFin;

    @ManyToMany
    @JoinTable(name = "facture_prestations",
               joinColumns = @JoinColumn(name = "facture_id"),
               inverseJoinColumns = @JoinColumn(name = "prestation_id"))
    private List<Prestation> prestations;

    private String cheminPdf;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Facture() {
    }

    public Facture(Long id, Entreprise client, LocalDate periodeDebut, LocalDate periodeFin,
                   List<Prestation> prestations, String cheminPdf, User user) {
        this.id = id;
        this.client = client;
        this.periodeDebut = periodeDebut;
        this.periodeFin = periodeFin;
        this.prestations = prestations;
        this.cheminPdf = cheminPdf;
        this.user = user;
    }

    public Facture(Long id, Entreprise client, LocalDate periodeDebut, LocalDate periodeFin,
                   List<Prestation> prestations, String cheminPdf, Long userId) {
        this.id = id;
        this.client = client;
        this.periodeDebut = periodeDebut;
        this.periodeFin = periodeFin;
        this.prestations = prestations;
        this.cheminPdf = cheminPdf;
        this.user = new User();
        this.user.setId(userId);  
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Entreprise getClient() {
        return client;
    }
    public void setClient(Entreprise client) {
        this.client = client;
    }
    public LocalDate getPeriodeDebut() {
        return periodeDebut;
    }
    public void setPeriodeDebut(LocalDate periodeDebut) {
        this.periodeDebut = periodeDebut;
    }
    public LocalDate getPeriodeFin() {
        return periodeFin;
    }
    public void setPeriodeFin(LocalDate periodeFin) {
        this.periodeFin = periodeFin;
    }
    public List<Prestation> getPrestations() {
        return prestations;
    }
    public void setPrestations(List<Prestation> prestations) {
        this.prestations = prestations;
    }
    public String getCheminPdf() {
        return cheminPdf;
    }
    public void setCheminPdf(String cheminPdf) {
        this.cheminPdf = cheminPdf;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal montantTotal() {
        return prestations.stream()
            .map(Prestation::calculerMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
