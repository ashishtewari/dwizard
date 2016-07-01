/**
 * 
 */
package com.mebelkart.api.cart.v1.core;

/**
 * @author Nikhil
 *
 */
public class CartProductDetailsWrapper {

	private Integer cartId;
	private Integer productId;
	private Integer productAttributeId;
	private Integer quantity;
	
	

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer cartId) {
		this.cartId = cartId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getProductAttributeId() {
		return productAttributeId;
	}

	public void setProductAttributeId(Integer productAttributeId) {
		this.productAttributeId = productAttributeId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
