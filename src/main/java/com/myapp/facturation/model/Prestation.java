package com.myapp.facturation.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Prestation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected LocalDate date;

    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    protected Entreprise client;

    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public Entreprise getClient() {
        return client;
    }
    public void setClient(Entreprise client) {
        this.client = client;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public abstract String getTypeName();

    public abstract java.math.BigDecimal calculerMontant();
}
