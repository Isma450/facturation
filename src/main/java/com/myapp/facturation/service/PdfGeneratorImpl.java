package com.myapp.facturation.service;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.myapp.facturation.model.Facture;
import com.myapp.facturation.model.Formation;
import com.myapp.facturation.model.Prestation;
import com.myapp.facturation.model.User;
import com.myapp.facturation.service.auth.UserService;


@Service
public class PdfGeneratorImpl implements PdfGenerator {

    private final UserService userService;

    public PdfGeneratorImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void generer(Facture facture, String cheminFichier) {
        User issuer = userService.getConnectedUser();

        try {
            String cheminComplet;
            if (cheminFichier.contains("/pdf/")) {
                cheminComplet = cheminFichier;
            } else {
                String repertoirePdf = System.getProperty("user.dir") + "/pdf/";
                java.nio.file.Path path = java.nio.file.Paths.get(repertoirePdf);
                if (!java.nio.file.Files.exists(path)) {
                    java.nio.file.Files.createDirectories(path);
                }
                cheminComplet = repertoirePdf + cheminFichier;
            }
            
            try (FileOutputStream fos = new FileOutputStream(cheminComplet)) {
                Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
                PdfWriter.getInstance(doc, fos);
                doc.open();

                Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                Font labelFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
                Font smallFont  = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);

                Paragraph titre = new Paragraph("Facture #" + facture.getId(), titleFont);
                titre.setAlignment(Element.ALIGN_CENTER);
                doc.add(titre);
                doc.add(Chunk.NEWLINE);

                PdfPTable infos = new PdfPTable(2);
                infos.setWidthPercentage(100);

                PdfPCell c1 = new PdfPCell();
                c1.setBorder(Rectangle.NO_BORDER);
                c1.addElement(new Paragraph("Client :", labelFont));
                c1.addElement(new Paragraph(facture.getClient().getNom(), normalFont));
                c1.addElement(new Paragraph(facture.getClient().getAdresse(), normalFont));
                infos.addCell(c1);

                PdfPCell c2 = new PdfPCell();
                c2.setBorder(Rectangle.NO_BORDER);
                c2.addElement(new Paragraph("Émetteur :", labelFont));
                c2.addElement(new Paragraph(issuer.getUsername(), normalFont));
                c2.addElement(new Paragraph(issuer.getAdresse(), normalFont));
                infos.addCell(c2);

                doc.add(infos);
                doc.add(Chunk.NEWLINE);

                doc.add(new Paragraph("Période : " + facture.getPeriodeDebut() + " → " + facture.getPeriodeFin(), normalFont));
                doc.add(Chunk.NEWLINE);

                PdfPTable table = new PdfPTable(new float[]{1, 4, 2, 1, 2, 2, 2, 2});
                table.setWidthPercentage(100);

                String[] headers = {"#", "Désignation", "Unité", "Qté", "Prix u. HT", "TVA", "Montant HT", "Montant TTC"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(BaseColor.DARK_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                BigDecimal totalHT = BigDecimal.ZERO;
                BigDecimal totalTTC = BigDecimal.ZERO;

                List<Prestation> prestations = facture.getPrestations();
                for (int i = 0; i < prestations.size(); i++) {
                    Prestation p = prestations.get(i);
                    BigDecimal montantHT = p.calculerMontant();
                    BigDecimal montantTVA = montantHT.multiply(new BigDecimal("0.20")).setScale(2);
                    BigDecimal montantTTC = montantHT.add(montantTVA);

                    totalHT = totalHT.add(montantHT);
                    totalTTC = totalTTC.add(montantTTC);

                    table.addCell(String.valueOf(i + 1));
                    table.addCell(p instanceof Formation ? ((Formation) p).getModule() : "Consultation");
                    table.addCell(p instanceof Formation ? "heure" : "");
                    table.addCell("1");
                    table.addCell(montantHT + " €");
                    table.addCell(montantTVA + " €");
                    table.addCell(montantHT + " €");
                    table.addCell(montantTTC + " €");
                }

                doc.add(table);
                doc.add(Chunk.NEWLINE);

                PdfPTable totaux = new PdfPTable(new float[]{6, 2});
                totaux.setWidthPercentage(100);

                addTotalRow(totaux, "Total HT", totalHT, normalFont);
                addTotalRow(totaux, "TVA 20%", totalTTC.subtract(totalHT), normalFont);
                addTotalRow(totaux, "Total TTC", totalTTC, normalFont);
                doc.add(totaux);

                doc.add(Chunk.NEWLINE);

                Paragraph conditions = new Paragraph(
                        "Paiement sous 30 jours - Retard : pénalité 3x taux légal - Aucun escompte - Paiement par virement",
                        smallFont
                );
                doc.add(conditions);

                doc.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF: " + e.getMessage(), e);
        }
    }

    private void addTotalRow(PdfPTable table, String label, BigDecimal amount, Font font) {
        PdfPCell l = new PdfPCell(new Phrase(label, font));
        l.setBorder(Rectangle.NO_BORDER);
        table.addCell(l);

        PdfPCell r = new PdfPCell(new Phrase(amount.setScale(2) + " €", font));
        r.setHorizontalAlignment(Element.ALIGN_RIGHT);
        r.setBorder(Rectangle.NO_BORDER);
        table.addCell(r);
    }
}
