/**
 * 
 */
package com.mebelkart.api.category.v1.core;

import java.util.List;

/**
 * @author Nikhil
 *
 */
public class SubCategoryDetailsWrapper {

	private Integer id;
	private String linkRewrite;
	private String name;
	private String image;
	private List<Object> children;
	
	
	public SubCategoryDetailsWrapper(Integer id, String linkRewrite,
			String name, String image, List<Object> children) {
		super();
		this.id = id;
		this.linkRewrite = linkRewrite;
		this.name = name;
		this.image = image;
		this.children = children;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLinkRewrite() {
		return linkRewrite;
	}

	public void setLinkRewrite(String linkRewrite) {
		this.linkRewrite = linkRewrite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Object> getChildren() {
		return children;
	}

	public void setChildren(List<Object> children) {
		this.children = children;
	}

}
