package com.myapp.facturation.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DownloadController {

    @GetMapping("/download/{filename}")
    public ResponseEntity<InputStreamResource> downloadPdf(@PathVariable String filename) throws IOException {
        
        
        String repertoirePdf = System.getProperty("user.dir") + "/pdf/";
        File file = new File(repertoirePdf + filename);
        
        if (!file.exists()) {
            System.err.println("Fichier PDF non trouvé : " + file.getAbsolutePath());
            return ResponseEntity.notFound().build();
        }
        
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(file.length())
                .body(resource);
    }
}
