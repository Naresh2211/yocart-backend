package com.ecommerce.project.dto;

public class OrderItemDTO {

    private Long id;
    private ProductDTO product;
    private int quantity;
    private double price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
