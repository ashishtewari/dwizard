/**
 * 
 */
package com.mebelkart.api.mobile.v1.core;

/**
 * @author Tinku
 *
 */
public class ProductDetailsWrapper {
	private String type;
	private String product_id;
	private String category_id;
	private int brand_id;
	private String imag_url;
	private String title;
	private String offer_text;
	private int is_sold_out;
	private int mkt_price;
	private int our_price;
	private String flash_sale_date_end;
	private String flash_sale_ends_in;
	
	public ProductDetailsWrapper(){
		
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}
	public int getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(int brand_id) {
		this.brand_id = brand_id;
	}
	public String getImag_url() {
		return imag_url;
	}
	public void setImag_url(String imag_url) {
		this.imag_url = imag_url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOffer_text() {
		return offer_text;
	}
	public void setOffer_text(String offer_text) {
		this.offer_text = offer_text;
	}
	public int getIs_sold_out() {
		return is_sold_out;
	}
	public void setIs_sold_out(int is_sold_out) {
		this.is_sold_out = is_sold_out;
	}
	public int getMkt_price() {
		return mkt_price;
	}
	public void setMkt_price(int mkt_price) {
		this.mkt_price = mkt_price;
	}
	public int getOur_price() {
		return our_price;
	}
	public void setOur_price(int our_price) {
		this.our_price = our_price;
	}
	public String getFlash_sale_date_end() {
		return flash_sale_date_end;
	}
	public void setFlash_sale_date_end(String flash_sale_date_end) {
		this.flash_sale_date_end = flash_sale_date_end;
	}

	public String getFlash_sale_ends_in() {
		return flash_sale_ends_in;
	}

	public void setFlash_sale_ends_in(String flash_sale_ends_in) {
		this.flash_sale_ends_in = flash_sale_ends_in;
	}
}
