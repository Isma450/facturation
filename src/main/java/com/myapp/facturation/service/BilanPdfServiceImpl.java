package com.myapp.facturation.service;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.myapp.facturation.model.BilanCA;
import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.model.User;
import com.myapp.facturation.service.auth.UserService;


@Service
public class BilanPdfServiceImpl implements BilanPdfService {

    private final UserService userService;

    public BilanPdfServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void genererPdf(BilanCA bilan, String cheminFichier) {
        User issuer = userService.getConnectedUser();

        try (FileOutputStream fos = new FileOutputStream(cheminFichier)) {
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, fos);
            doc.open();

            Paragraph titre = new Paragraph(
                    "Bilan CA du " + bilan.getDebut() + " au " + bilan.getFin(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
            );
            titre.setAlignment(Element.ALIGN_CENTER);
            doc.add(titre);
            doc.add(Chunk.NEWLINE);

            PdfPTable info = new PdfPTable(1);
            info.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.addElement(new Paragraph(issuer.getUsername(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
            cell.addElement(new Paragraph(issuer.getAdresse(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
            info.addCell(cell);
            doc.add(info);
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(new float[]{3,2});
            table.setWidthPercentage(100);
            table.addCell(headerCell("Client"));
            table.addCell(headerCell("CA (€)"));

            BigDecimal total = BigDecimal.ZERO;
            for (Map.Entry<Entreprise, BigDecimal> entry : bilan.getCaParClient().entrySet()) {
                table.addCell(entry.getKey().getNom());
                BigDecimal ca = entry.getValue().setScale(2);
                table.addCell(ca.toString());
                total = total.add(ca);
            }

            PdfPCell totalLabel = new PdfPCell(new Phrase("Total général:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            totalLabel.setBorder(Rectangle.TOP);
            table.addCell(totalLabel);

            PdfPCell totalVal = new PdfPCell(new Phrase(total.setScale(2).toString(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            totalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalVal.setBorder(Rectangle.TOP);
            table.addCell(totalVal);

            doc.add(table);
            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF bilan", e);
        }
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        cell.setBackgroundColor(BaseColor.GRAY);
        return cell;
    }
}
