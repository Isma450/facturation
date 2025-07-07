package com.myapp.facturation.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myapp.facturation.dto.PrestationForm;
import com.myapp.facturation.model.Consultation;
import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.model.Facture;
import com.myapp.facturation.model.Formation;
import com.myapp.facturation.model.Prestation;
import com.myapp.facturation.model.User;
import com.myapp.facturation.repository.EntrepriseRepository;
import com.myapp.facturation.repository.FactureRepository;
import com.myapp.facturation.repository.PrestationRepository;
import com.myapp.facturation.service.FactureService;
import com.myapp.facturation.service.auth.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PrestationWebController {

    private final PrestationRepository prestationRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final FactureRepository factureRepository;
    private final FactureService factureService;
    private final UserService userService;
    
    // Constructeur manuel
    public PrestationWebController(PrestationRepository prestationRepository,
                                 EntrepriseRepository entrepriseRepository,
                                 FactureRepository factureRepository,
                                 FactureService factureService,
                                 UserService userService) {
        this.prestationRepository = prestationRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.factureRepository = factureRepository;
        this.factureService = factureService;
        this.userService = userService;
    }

    /**
     * Page principale des prestations (équivalent PrestationPanel)
     */
    @GetMapping("/prestations")
    public String prestations(@RequestParam(required = false) Long clientId, Model model) {
        
        List<Entreprise> entreprises = entrepriseRepository.findAll();
        model.addAttribute("entreprises", entreprises);
        
        
        Entreprise clientSelectionne = null;
        if (clientId != null) {
            clientSelectionne = entrepriseRepository.findById(clientId).orElse(null);
        }
        model.addAttribute("clientSelectionne", clientSelectionne);
        
        
        List<Prestation> prestations;
        if (clientSelectionne != null) {
            prestations = prestationRepository.findByClient(clientSelectionne);
        } else {
            prestations = prestationRepository.findAll();
        }
        model.addAttribute("prestations", prestations);
        
        
        model.addAttribute("prestationForm", new PrestationForm());
        
        return "prestation";
    }
    
    /**
     * Ajouter une nouvelle prestation (équivalent onAjouter())
     */
    @PostMapping("/prestations/ajouter")
    public String ajouterPrestation(@ModelAttribute PrestationForm form, 
                                   RedirectAttributes redirectAttributes) {
        try {
            
            Entreprise client = entrepriseRepository.findById(form.getClientId())
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
            
            
            Prestation prestation;
            if ("Formation".equals(form.getType())) {
                Formation formation = new Formation();
                formation.setDate(form.getDate());
                formation.setClient(client);
                formation.setHeureDebut(form.getHeureDebut());
                formation.setHeureFin(form.getHeureFin());
                formation.setClasse(form.getClasse());
                formation.setModule(form.getModule());
                prestation = formation;
            } else { 
                Consultation consultation = new Consultation();
                consultation.setDate(form.getDate());
                consultation.setClient(client);
                consultation.setTjm(form.getTjm());
                prestation = consultation;
            }
            
            
            prestation.setUser(userService.getConnectedUser());
            
            prestationRepository.save(prestation);
            redirectAttributes.addFlashAttribute("success", "Prestation ajoutée avec succès");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        
        return "redirect:/prestations";
    }
    
    /**
     * Page de facturation (équivalent FacturePanel)
     */
    @GetMapping("/factures")
    public String factures(@RequestParam(required = false) Long clientId, Model model) {
        
        List<Entreprise> clients = entrepriseRepository.findAll();
        model.addAttribute("clients", clients);
        
       
        final Entreprise clientSelectionne;
        if (clientId != null) {
            clientSelectionne = entrepriseRepository.findById(clientId).orElse(null);
        } else {
            clientSelectionne = null;
        }
        model.addAttribute("clientSelectionne", clientSelectionne);
        
        
        List<Prestation> prestations;
        if (clientSelectionne != null) {
            List<Prestation> toutesLesPrestations = prestationRepository.findByClient(clientSelectionne);
            
            
            List<Facture> facturesClient = factureRepository.findAll().stream()
                .filter(f -> f.getClient() != null && f.getClient().getId().equals(clientSelectionne.getId()))
                .toList();
            
            
            List<Long> prestationsFactureesIds = facturesClient.stream()
                .flatMap(f -> f.getPrestations().stream())
                .map(Prestation::getId)
                .toList();
            
            
            prestations = toutesLesPrestations.stream()
                .filter(p -> !prestationsFactureesIds.contains(p.getId()))
                .toList();
        } else {
            prestations = List.of(); 
        }
        model.addAttribute("prestations", prestations);
        
        
        List<Facture> factures;
        if (clientSelectionne != null) {
            factures = factureRepository.findAll().stream()
                .filter(f -> f.getClient() != null && f.getClient().getId().equals(clientSelectionne.getId()))
                .toList();
        } else {
            factures = List.of();
        }
        model.addAttribute("factures", factures);
        
        return "facturation";
    }
    
    /**
     * Facturer une prestation (équivalent onFacturerAction)
     */
    @PostMapping("/factures/prestation/{id}")
    public String facturerPrestation(@PathVariable Long id, 
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        try {
            System.out.println(">>> Début facturation prestation ID: " + id);
            
            Prestation prestation = prestationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestation introuvable"));
            
            System.out.println(">>> Prestation trouvée: " + prestation.getId());
            
            
            User currentUser = (User) request.getAttribute("currentUser");
            if (currentUser == null) {
                throw new RuntimeException("Utilisateur non connecté");
            }
            
            System.out.println(">>> Utilisateur connecté: " + currentUser.getId());
            
            
            Facture facture = factureService.creerFacturePourPrestationAvecUser(prestation, currentUser);
            
            System.out.println(">>> Facture créée avec ID: " + facture.getId());
            
            
            factureService.exporterPdf(facture);
            
            System.out.println(">>> PDF exporté avec succès");
            
            redirectAttributes.addFlashAttribute("success", 
                "Facture créée avec succès ! ID: " + facture.getId());
            
            
            return "redirect:/factures?clientId=" + prestation.getClient().getId();
            
        } catch (Exception e) {
            System.out.println(">>> ERREUR lors de la facturation: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/factures";
        }
    }
    
    /**
     * Prévisualiser une facture pour une prestation
     */
    @GetMapping("/factures/preview/{id}")
    public String previewFacturePrestation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Prestation prestation = prestationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestation introuvable"));
            
            
            Facture factureTemp = new Facture(
                null,
                prestation.getClient(),
                prestation.getDate(),
                prestation.getDate(),
                List.of(prestation),
                "preview_facture_" + System.currentTimeMillis() + ".pdf",
                prestation.getUser().getId()
            );
            
            
            factureService.exporterPdf(factureTemp);
            
            
            return "redirect:/download/" + factureTemp.getCheminPdf();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la prévisualisation : " + e.getMessage());
            return "redirect:/factures";
        }
    }

    /**
     * Télécharger une facture PDF
     */
    @GetMapping("/factures/pdf/{id}")
    public String telechargerFacturePdf(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture introuvable"));
            
            
            String repertoirePdf = System.getProperty("user.dir") + "/pdf/";
            java.io.File file = new java.io.File(repertoirePdf + facture.getCheminPdf());
            
            if (!file.exists()) {
                System.out.println("PDF manquant pour la facture " + id + ", génération en cours...");
                factureService.exporterPdf(facture);
            }
            
            
            return "redirect:/download/" + facture.getCheminPdf();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du téléchargement : " + e.getMessage());
            return "redirect:/factures";
        }
    }
    
    /**
     * Page d'accueil
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/prestations/facturer")
    public String facturerPrestations(@RequestParam Long clientId,
                                     @RequestParam List<Long> prestationsIds,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        try {
            System.out.println("DEBUG: Facturation groupée - clientId=" + clientId + ", prestationsIds=" + prestationsIds);
            
            
            User currentUser = userService.getConnectedUser();
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "Session expirée. Veuillez vous reconnecter.");
                return "redirect:/login";
            }
            
            
            Entreprise client = entrepriseRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            
            
            List<Prestation> prestations = prestationRepository.findAllById(prestationsIds);
            
            if (prestations.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Aucune prestation sélectionnée");
                return "redirect:/factures?clientId=" + clientId;
            }
            
            
            Facture facture = null;
            for (Prestation prestation : prestations) {
                facture = factureService.creerFacturePourPrestationAvecUser(prestation, currentUser);
                factureService.exporterPdf(facture);
            }
            
            redirectAttributes.addFlashAttribute("success", 
                "Facture(s) créée(s) avec succès !");
            
            return "redirect:/factures?clientId=" + clientId;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la facturation groupée: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la création de la facture: " + e.getMessage());
            return "redirect:/factures?clientId=" + clientId;
        }
    }

    @PostMapping("/prestations/apercu")
    public String apercuPrestations(@RequestParam Long clientId,
                                   @RequestParam List<Long> prestationsIds,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        try {
            System.out.println("DEBUG: Aperçu groupé - clientId=" + clientId + ", prestationsIds=" + prestationsIds);
            
            
            User currentUser = userService.getConnectedUser();
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "Session expirée. Veuillez vous reconnecter.");
                return "redirect:/login";
            }
            
           
            List<Prestation> prestations = prestationRepository.findAllById(prestationsIds);
            
            if (prestations.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Aucune prestation sélectionnée");
                return "redirect:/factures?clientId=" + clientId;
            }
            
            
            Facture factureTemp = factureService.creerFacturePourPrestationAvecUser(prestations.get(0), currentUser);
            
            
            factureService.exporterPdf(factureTemp);
            
            
            return "redirect:/factures/pdf/" + factureTemp.getId();
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'aperçu groupé: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la prévisualisation: " + e.getMessage());
            return "redirect:/factures?clientId=" + clientId;
        }
    }
}
