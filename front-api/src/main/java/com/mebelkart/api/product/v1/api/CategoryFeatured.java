package com.mebelkart.api.product.v1.api;

/**
 * Created by vinitpayal on 27/04/16.
 */
public class CategoryFeatured {
    private String type;
    private String startPrice;
    private Integer categoryId;
    private String categoryName;
    private Object children;

    public CategoryFeatured(String type, String startPrice, Integer categoryId, String categoryName, Object children) {
        this.type = type;
        this.startPrice = startPrice;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.children = children;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(String startPrice) {
        this.startPrice = startPrice;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Object getChildren() {
        return children;
    }

    public void setChildren(Object children) {
        this.children = children;
    }
}