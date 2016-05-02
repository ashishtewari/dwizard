/**
 * 
 */
package com.mebelkart.api.product.v1.api;

/**
 * @author Tinku
 *
 */
public class ProductDetailsResponse {

	private String productId;
	private String categoryId;
	private String categoryName;
	private String productName;
	private String productDesc;
	private String availLocation;
	private String brandId;
	private String brandName;
	private String totalViews;
	private String shippingCost;
	private String shippingAvailable;
	private Object[] gallery;
	private String offerText;
	private double mktPrice;
	private double ourPrice;
	private String emiPrice;
	private int rating;
	private String attributes;
	private Object productFeatures;
	private int isSoldOut;
	private Object attributeGroups;
	private String[] reviews;
	private int totalReviews;
	private int totalReviewsCount;
	
	/**
	 * Parameterized Constructor
	 */
	public ProductDetailsResponse(String productId,String categoryId,String categoryName,String productName,String productDesc,String availLocation,String brandId,String brandName,String totalViews,String shippingCost,String shippingAvailable,Object[] gallery,String offerText,double mktPrice,double ourPrice,String emiPrice,int rating,String attributes,Object productFeatures,int isSoldOut,Object attributeGroups,String[] reviews,int totalReviews,int totalReviewsCount) {
		// TODO Auto-generated constructor stub
		this.setProductId(productId);
		this.setCategoryId(categoryId);
		this.setCategoryName(categoryName);
		this.setProductName(productName);
		this.setProductDesc(productDesc);
		this.setAvailLocation(availLocation);
		this.setBrandId(brandId);
		this.setBrandName(brandName);
		this.setTotalViews(totalViews);
		this.setShippingCost(shippingCost);
		this.setShippingAvailable(shippingAvailable);
		this.setGallery(gallery);
		this.setOfferText(offerText);
		this.setMktPrice(mktPrice);
		this.setOurPrice(ourPrice);
		this.setEmiPrice(emiPrice);
		this.setRating(rating);
		this.setAttributes(attributes);
		this.setProductFeatures(productFeatures);
		this.setIsSoldOut(isSoldOut);
		this.setAttributeGroups(attributeGroups);
		this.setReviews(reviews);
		this.setTotalReviews(totalReviews);
		this.setTotalReviewsCount(totalReviewsCount);
	}

	/**
	 * Default Constructer
	 */
	public ProductDetailsResponse() {
		// TODO Auto-generated constructor stub
		this.setProductId(null);
		this.setCategoryId(null);
		this.setCategoryName(null);
		this.setProductName(null);
		this.setProductDesc(null);
		this.setAvailLocation(null);
		this.setBrandId(null);
		this.setBrandName(null);
		this.setTotalViews(null);
		this.setShippingCost(null);
		this.setShippingAvailable(null);
		this.setGallery(null);
		this.setOfferText(null);
		this.setMktPrice(0.0);
		this.setOurPrice(0.0);
		this.setEmiPrice(null);
		this.setRating(0);
		this.setAttributes(null);
		this.setProductFeatures(null);
		this.setIsSoldOut(0);
		this.setAttributeGroups(null);
		this.setReviews(null);
		this.setTotalReviews(0);
		this.setTotalReviewsCount(0);
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getAvailLocation() {
		return availLocation;
	}

	public void setAvailLocation(String availLocation) {
		this.availLocation = availLocation;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getTotalViews() {
		return totalViews;
	}

	public void setTotalViews(String totalViews) {
		this.totalViews = totalViews;
	}

	public String getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(String shippingCost) {
		this.shippingCost = shippingCost;
	}

	public String getShippingAvailable() {
		return shippingAvailable;
	}

	public void setShippingAvailable(String shippingAvailable) {
		this.shippingAvailable = shippingAvailable;
	}

	public Object[] getGallery() {
		return gallery;
	}

	public void setGallery(Object[] gallery) {
		this.gallery = gallery;
	}

	public String getOfferText() {
		return offerText;
	}

	public void setOfferText(String offerText) {
		this.offerText = offerText;
	}

	public double getMktPrice() {
		return mktPrice;
	}

	public void setMktPrice(double mktPrice) {
		this.mktPrice = mktPrice;
	}

	public double getOurPrice() {
		return ourPrice;
	}

	public void setOurPrice(double ourPrice) {
		this.ourPrice = ourPrice;
	}

	public String getEmiPrice() {
		return emiPrice;
	}

	public void setEmiPrice(String emiPrice) {
		this.emiPrice = emiPrice;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public Object getProductFeatures() {
		return productFeatures;
	}

	public void setProductFeatures(Object productFeatures) {
		this.productFeatures = productFeatures;
	}

	public int getIsSoldOut() {
		return isSoldOut;
	}

	public void setIsSoldOut(int isSoldOut) {
		this.isSoldOut = isSoldOut;
	}

	public Object getAttributeGroups() {
		return attributeGroups;
	}

	public void setAttributeGroups(Object attributeGroups) {
		this.attributeGroups = attributeGroups;
	}

	public String[] getReviews() {
		return reviews;
	}

	public void setReviews(String[] reviews) {
		this.reviews = reviews;
	}

	public int getTotalReviews() {
		return totalReviews;
	}

	public void setTotalReviews(int totalReviews) {
		this.totalReviews = totalReviews;
	}

	public int getTotalReviewsCount() {
		return totalReviewsCount;
	}

	public void setTotalReviewsCount(int totalReviewsCount) {
		this.totalReviewsCount = totalReviewsCount;
	}
}
