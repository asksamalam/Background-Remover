package com.bgai.bgai.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {
    @PostMapping("/photo")
    public String uploadPhoto(@RequestParam MultipartFile file , Model model){
        if(file.isEmpty()){
            model.addAttribute("message", "No file Selected!");
            return "File is empty";
        }
        try {
            // Define a directory to save the uploaded image
            String uploadDirectory = "uploads/";

            // Create the directory if it doesn't exist
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdir();
            }

            // Define the path to save the file
            String filePath = uploadDirectory + file.getOriginalFilename();

            // Save the file
            file.transferTo(new File(filePath));

            model.addAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());
            model.addAttribute("imagePath", filePath); // Return the file path for the user to see the image

        } catch (IOException e) {
            model.addAttribute("message", "Error uploading file: " + e.getMessage());
        }

        return "uploadForm";

    }

    // Define the directory where images are stored
    private final Path imageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    // API to get image by filename
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            // Load image as resource
            Path filePath = imageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                // Return the image with content type "image/jpeg" or the appropriate type
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
