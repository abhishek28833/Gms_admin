package com.admin.controller;

import com.admin.controller.base.GlobalApiResponse;
import com.admin.model.dto.AddProductDto;
import com.admin.model.entity.UserProductDetails;
import com.admin.service.AddProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ReceiptsUpload {

    @Autowired
    private AddProductService addProductService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProduct(@ModelAttribute @Valid AddProductDto addProductDto, HttpServletRequest request){
        String userId = request.getAttribute("userId").toString();
        try {
            UserProductDetails savedProduct = addProductService.saveProduct(addProductDto,userId);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }
}
