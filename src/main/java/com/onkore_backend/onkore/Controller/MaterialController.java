package com.onkore_backend.onkore.Controller;

import com.onkore_backend.onkore.Model.Material;
import com.onkore_backend.onkore.Service.Data.DeleteServices;
import com.onkore_backend.onkore.Service.Data.GetServices;
import com.onkore_backend.onkore.Service.Data.PutServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    @Autowired
    private GetServices getServices;

    @Autowired
    private PutServices putServices;

    @Autowired
    private DeleteServices deleteServices;


    @PutMapping(value = "/{courseId}/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Material> uploadPdf(@PathVariable String courseId, @RequestParam("file") MultipartFile file) throws IOException {
        try {
            Material newMaterial = putServices.uploadPdfAndAddToCourse(courseId, file);
            return ResponseEntity.ok(newMaterial);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping("/materials/{materialId}/download")
    public ResponseEntity<InputStreamResource> downloadPdf(@PathVariable String materialId) throws IOException {
        System.out.println("Material: " + materialId);
        Material material = getServices.getMaterial(materialId);
        Path path = Paths.get(material.getFilePath());
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        // Prepare stream
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

        // Prepare filename for the response
        String originalFilename = material.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "file.pdf";
        }

        // Set headers so browser knows it's a downloadable file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(Files.size(path))
                .body(resource);
    }

    // 3) Delete a Material from a Course (and optionally from disk)
    @DeleteMapping("/{courseId}/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable String courseId,
            @PathVariable String materialId,
            @RequestParam(name = "deleteFile", defaultValue = "true") boolean deleteFile
    ) {
        deleteServices.deleteMaterialFromCourse(courseId, materialId, deleteFile);
        return ResponseEntity.noContent().build();
    }
}
