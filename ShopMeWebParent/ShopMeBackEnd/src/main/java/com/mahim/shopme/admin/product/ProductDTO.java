package com.mahim.shopme.admin.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductDTO {
    private String name;
    private String imagePath;
    private float price;
    private float cost;
}
