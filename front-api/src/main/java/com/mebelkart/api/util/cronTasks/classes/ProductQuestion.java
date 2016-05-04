package com.mebelkart.api.util.cronTasks.classes;

/**
 * Created by vinitpayal on 03/05/16.
 */
public class ProductQuestion {
    private String idQna;
    private String question;
    private String email;
    private String customerName;
    private String idProduct;
    private Integer approved;
    private String dateAdded;
    private String answer;

    public String getIdQna() {
        return idQna;
    }

    public void setIdQna(String idQna) {
        this.idQna = idQna;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public Integer getApproved() {
        return approved;
    }

    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ProductQuestion(String idQna, String question, String email, String customerName, String idProduct
            , Integer approved, String dateAdded, String answer) {
        this.idQna = idQna;
        this.question = question;
        this.email = email;
        this.customerName = customerName;
        this.idProduct = idProduct;
        this.approved = approved;
        this.dateAdded = dateAdded;
        this.answer = answer;
    }
}
