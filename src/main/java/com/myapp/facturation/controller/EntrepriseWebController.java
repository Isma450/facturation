package com.myapp.facturation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myapp.facturation.model.Entreprise;
import com.myapp.facturation.repository.EntrepriseRepository;

@Controller
@RequestMapping("/entreprises")
public class EntrepriseWebController {
    
    private final EntrepriseRepository entrepriseRepository;
    
    public EntrepriseWebController(EntrepriseRepository entrepriseRepository) {
        this.entrepriseRepository = entrepriseRepository;
    }
    
    /**
     * Lister toutes les entreprises
     */
    @GetMapping
    public String listerEntreprises(Model model) {
        model.addAttribute("entreprises", entrepriseRepository.findAll());
        return "entreprises";
    }
    
    /**
     * Afficher le formulaire de création d'entreprise (équivalent EnterpriseDialog)
     */
    @GetMapping("/nouveau")
    public String nouveauClient(Model model) {
        model.addAttribute("entreprise", new Entreprise());
        return "entreprise-nouveau";
    }
    
    /**
     * Afficher le formulaire d'édition d'une entreprise
     */
    @GetMapping("/edit/{id}")
    public String editerClient(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
            model.addAttribute("entreprise", entreprise);
            model.addAttribute("isEdit", true);
            return "entreprise-nouveau";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Client introuvable : " + e.getMessage());
            return "redirect:/entreprises";
        }
    }
    
    /**
     * Enregistrer une nouvelle entreprise ou mettre à jour une existante
     */
    @PostMapping("/ajouter")
    public String ajouterEntreprise(@ModelAttribute Entreprise entreprise, 
                                   RedirectAttributes redirectAttributes) {
        try {
            
            if (entreprise.getNom() == null || entreprise.getNom().trim().isEmpty() ||
                entreprise.getAdresse() == null || entreprise.getAdresse().trim().isEmpty() ||
                entreprise.getSiret() == null || entreprise.getSiret().trim().isEmpty() ||
                entreprise.getEmail() == null || entreprise.getEmail().trim().isEmpty()) {
                
                redirectAttributes.addFlashAttribute("error", 
                    "Tous les champs sont obligatoires.");
                return "redirect:/entreprises/nouveau";
            }
            
            
            if (!entreprise.getSiret().matches("\\d{14}")) {
                redirectAttributes.addFlashAttribute("error", 
                    "Le SIRET doit contenir exactement 14 chiffres.");
                return "redirect:/entreprises/nouveau";
            }
            
            
            if (!entreprise.getEmail().matches("^.+@.+\\..+$")) {
                redirectAttributes.addFlashAttribute("error", 
                    "Format d'email invalide.");
                return "redirect:/entreprises/nouveau";
            }
            
            
            entrepriseRepository.save(entreprise);
            
            String message = entreprise.getId() != null ? 
                "Client modifié avec succès : " + entreprise.getNom() :
                "Client créé avec succès : " + entreprise.getNom();
            
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/entreprises";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de l'enregistrement : " + e.getMessage());
            return "redirect:/entreprises/nouveau";
        }
    }
    
    /**
     * Supprimer une entreprise
     */
    @GetMapping("/delete/{id}")
    public String supprimerClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
            
            String nomEntreprise = entreprise.getNom();
            entrepriseRepository.delete(entreprise);
            
            redirectAttributes.addFlashAttribute("success", 
                "Client supprimé avec succès : " + nomEntreprise);
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la suppression : " + e.getMessage());
        }
        
        return "redirect:/entreprises";
    }
}
