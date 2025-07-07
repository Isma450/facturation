package com.myapp.facturation.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myapp.facturation.model.BilanCA;
import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.repository.EntrepriseRepository;
import com.myapp.facturation.service.BilanPdfService;
import com.myapp.facturation.service.BilanService;

@Controller
@RequestMapping("/bilan")
public class BilanController {

    private final BilanService bilanService;
    private final BilanPdfService bilanPdfService;
    private final EntrepriseRepository entrepriseRepository;

    public BilanController(BilanService bilanService, BilanPdfService bilanPdfService, EntrepriseRepository entrepriseRepository) {
        this.bilanService = bilanService;
        this.bilanPdfService = bilanPdfService;
        this.entrepriseRepository = entrepriseRepository;
    }

    /**
     * Page principale du bilan avec formulaire
     */
    @GetMapping
    public String bilanForm(@RequestParam(required = false) String clientId,
                           @RequestParam(required = false) String mode,
                           @RequestParam(required = false) Integer mois,
                           @RequestParam(required = false) Integer annee,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
                           Model model) {
        
        
        model.addAttribute("clients", entrepriseRepository.findAll());
        model.addAttribute("moisList", getMoisList());
        model.addAttribute("annees", getAnneesList());
        
        
        if (mode != null && !mode.isEmpty()) {
            try {
                BilanCA bilan = null;
                Entreprise client = null;
                
                
                if (clientId != null && !clientId.isEmpty()) {
                    client = entrepriseRepository.findById(Long.parseLong(clientId)).orElse(null);
                }
                
                switch (mode) {
                    case "Annuel":
                        if (annee != null) {
                            bilan = bilanService.calculerBilanAnnuel(client, annee);
                        }
                        break;
                    case "Mensuel":
                        if (mois != null && annee != null) {
                            bilan = bilanService.calculerBilanMensuel(client, YearMonth.of(annee, mois));
                        }
                        break;
                    case "Periode":
                        if (debut != null && fin != null) {
                            bilan = bilanService.calculerBilanPeriode(client, debut, fin);
                        }
                        break;
                }
                
                if (bilan != null) {
                    model.addAttribute("bilan", bilan);
                }
            } catch (Exception e) {
                model.addAttribute("error", "Erreur lors du calcul du bilan : " + e.getMessage());
            }
        }
        
        return "bilan";
    }

    @GetMapping("/mensuel")
    public String bilanMensuel(@RequestParam Long clientId, @RequestParam int year, @RequestParam int month, Model model) {
        Entreprise client = new Entreprise();
        client.setId(clientId);
        BilanCA bilan = bilanService.calculerBilanMensuel(client, YearMonth.of(year, month));
        model.addAttribute("bilan", bilan);
        return "bilan/result";
    }

    @GetMapping("/annuel")
    public String bilanAnnuel(@RequestParam Long clientId, @RequestParam int year, Model model) {
        Entreprise client = new Entreprise();
        client.setId(clientId);
        BilanCA bilan = bilanService.calculerBilanAnnuel(client, year);
        model.addAttribute("bilan", bilan);
        return "bilan/result";
    }

    @GetMapping("/periode")
    public String bilanParPeriode(@RequestParam Long clientId,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
                                  Model model) {
        Entreprise client = new Entreprise();
        client.setId(clientId);
        BilanCA bilan = bilanService.calculerBilanPeriode(client, debut, fin);
        model.addAttribute("bilan", bilan);
        return "bilan/result";
    }

    @GetMapping("/export")
    public String exporterPdf(@RequestParam Long clientId,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        Entreprise client = new Entreprise();
        client.setId(clientId);
        BilanCA bilan = bilanService.calculerBilanPeriode(client, debut, fin);
        bilanPdfService.genererPdf(bilan, "bilan_export.pdf");
        return "redirect:/bilan/periode?clientId=" + clientId + "&debut=" + debut + "&fin=" + fin;
    }

    @PostMapping("/pdf")
    public ResponseEntity<Resource> exporterBilanPdf(@RequestParam(required = false) String clientId,
                                                     @RequestParam(required = false) String mode,
                                                     @RequestParam(required = false) Integer mois,
                                                     @RequestParam(required = false) Integer annee,
                                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
                                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        try {
            BilanCA bilan = null;
            Entreprise client = null;
            
            
            if (clientId != null && !clientId.isEmpty()) {
                client = entrepriseRepository.findById(Long.parseLong(clientId)).orElse(null);
            }
            
            
            if (mode != null) {
                switch (mode) {
                    case "Annuel":
                        if (annee != null) {
                            bilan = bilanService.calculerBilanAnnuel(client, annee);
                        }
                        break;
                    case "Mensuel":
                        if (mois != null && annee != null) {
                            bilan = bilanService.calculerBilanMensuel(client, YearMonth.of(annee, mois));
                        }
                        break;
                    case "Periode":
                        if (debut != null && fin != null) {
                            bilan = bilanService.calculerBilanPeriode(client, debut, fin);
                        }
                        break;
                }
            }
            
            if (bilan == null) {
                throw new IllegalArgumentException("Impossible de générer le bilan avec les paramètres fournis");
            }
            
            
            String nomFichier = "bilan_" + bilan.getDebut() + "_" + bilan.getFin() + ".pdf";
            String cheminFichier = "pdf/" + nomFichier;
            
            
            bilanPdfService.genererPdf(bilan, cheminFichier);
            
            
            Path path = Paths.get(cheminFichier);
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomFichier + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Le fichier PDF n'a pas pu être généré");
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    
    private List<Map<String, Object>> getMoisList() {
        List<Map<String, Object>> mois = new ArrayList<>();
        String[] noms = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        
        for (int i = 0; i < 12; i++) {
            Map<String, Object> moisMap = new HashMap<>();
            moisMap.put("value", i + 1);
            moisMap.put("name", noms[i]);
            mois.add(moisMap);
        }
        return mois;
    }

    private List<Integer> getAnneesList() {
        List<Integer> annees = new ArrayList<>();
        int anneeActuelle = LocalDate.now().getYear();
        for (int i = anneeActuelle - 5; i <= anneeActuelle + 1; i++) {
            annees.add(i);
        }
        return annees;
    }
}
