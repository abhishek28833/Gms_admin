package com.admin.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AddProductDto {

    private String productName;
    private String productDescription;
    private Double price;
    private MultipartFile receiptFile;

}
