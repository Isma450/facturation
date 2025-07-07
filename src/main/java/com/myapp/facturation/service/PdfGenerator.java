package com.myapp.facturation.service;

import com.myapp.facturation.model.Facture;

public interface PdfGenerator {
    void generer(Facture facture, String chemin);
}
