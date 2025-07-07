package com.myapp.facturation.service;

import com.myapp.facturation.model.BilanCA;

public interface BilanPdfService {
    void genererPdf(BilanCA bilan, String chemin);
}
