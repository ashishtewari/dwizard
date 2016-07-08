/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.core;

/**
 * @author Tinku
 *
 */
public class NBProductsWrapper {
	private int nbProducts;
	private int wishListId;
	
	public NBProductsWrapper (int nbProducts, int wishListId){
		this.setNbProducts(nbProducts);
		this.setWishListId(wishListId);
	}

	public int getNbProducts() {
		return nbProducts;
	}

	public void setNbProducts(int nbProducts) {
		this.nbProducts = nbProducts;
	}

	public int getWishListId() {
		return wishListId;
	}

	public void setWishListId(int wishListId) {
		this.wishListId = wishListId;
	}
}
