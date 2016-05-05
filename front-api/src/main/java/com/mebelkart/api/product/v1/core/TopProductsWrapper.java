package com.mebelkart.api.product.v1.core;

public class TopProductsWrapper {
	
	private int productId;
	private int productQuantity;
	
	public TopProductsWrapper(int productId,int productQuantity) {
		this.setProductId(productId);
		this.setProductQuantity(productQuantity);
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}

}
