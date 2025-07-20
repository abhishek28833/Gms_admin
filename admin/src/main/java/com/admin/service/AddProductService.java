package com.admin.service;


import com.admin.exceptions.GmsExceptionHandler;
import com.admin.model.dto.AddProductDto;
import com.admin.model.entity.UserProductDetails;
import com.admin.repository.UserDetailRepo;
import com.admin.repository.UserProductDetailsRepo;
import com.admin.utils.FileSizeIntoBytesHelper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.management.MXBean;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AddProductService {


    private static final String UPLOAD_DIR = "A:/GMS Project/admin/admin/src/main/resources/uploads/";
    private final String maxFileSize = "3mb";
    private final List<String> allowedExtensions = List.of("png", "jpeg");


    @Autowired
    private UserProductDetailsRepo userProductDetailsRepo;

    @Autowired
    private UserDetailRepo userDetailRepo;
    @Autowired
    private FileSizeIntoBytesHelper fileSizeIntoBytesHelper;

    public UserProductDetails saveProduct(AddProductDto addProduct,String userId) throws IOException {
        // Validate file type
        if(addProduct.getReceiptFile() == null || addProduct.getReceiptFile().getContentType() == null) {
            String errorMessage = "File is null or the content type is null ";
            throw new GmsExceptionHandler("RP-105");
//            throw new IllegalArgumentException("File is null or the content type is null ");
        }

        if(!addProduct.getReceiptFile().getContentType().equals("image/png") && !addProduct.getReceiptFile().getContentType().equals("image/jpeg")){
            throw new IllegalArgumentException("Only PNG or JPG images are allowed");
        }

        Long fileSize = addProduct.getReceiptFile().getSize();
        Long maxFileSizeStored = fileSizeIntoBytesHelper.convertFileSizeToBytes(maxFileSize);

        if(fileSize > maxFileSizeStored){
            String errorMessage = "Uploaded file size should be less than " + maxFileSize + ".";
            throw new GmsExceptionHandler(errorMessage,"RP-103");
        }

        String fileExtension = getFileExtension(addProduct.getReceiptFile().getOriginalFilename());
        if(!allowedExtensions.contains(fileExtension)){
            throw new GmsExceptionHandler("RP-104");
        }

        // Create directory if not exists
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Save file to disk
        String fileName = UUID.randomUUID().toString() + "_" + addProduct.getReceiptFile().getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.copy(addProduct.getReceiptFile().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        UserProductDetails userProductDetails = UserProductDetails.builder()
                .productName(addProduct.getProductName())
                .userId(userId)
                .productDescription(addProduct.getProductDescription())
                .productPrice(addProduct.getPrice())
                .receiptImageUrl("/uploads/" + fileName)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        return userProductDetailsRepo.save(userProductDetails);

    }

    public String getFileExtension(String fileName){
        String extension = "";
        if(fileName != null && fileName.contains(".")){
            extension = fileName.substring(fileName.lastIndexOf(".")+ 1).toLowerCase();
        }
        return extension;
    }
}
