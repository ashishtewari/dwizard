/**
 * 
 */
package com.mebelkart.api.product.v1.core;

/**
 * @author Tinku
 *
 */
public class ProductReviewsWrapper {
	
	private int reviewId;
	private String reviewTitle;
	private String reviewContent;
	private int reviewRating;
	private String reviewPermalink;
	private String reviewAddDate;
	private int reviewForOrderId;
	private String reviewer;

	/**
	 * Constructer
	 */
	public ProductReviewsWrapper(int reviewId,String reviewTitle,String reviewContent,int reviewRating,String reviewPermalink,
			String reviewAddDate,int reviewForOrderId,String reviewer) {
		this.setReviewId(reviewId);
		this.setReviewTitle(reviewTitle);
		this.setReviewContent(reviewContent);
		this.setReviewRating(reviewRating);
		this.setReviewPermalink(reviewPermalink);
		this.setReviewAddDate(reviewAddDate);
		this.setReviewForOrderId(reviewForOrderId);
		this.setReviewer(reviewer);
		
	}

	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public String getReviewTitle() {
		return reviewTitle;
	}

	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}

	public String getReviewContent() {
		return reviewContent;
	}

	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}

	public int getReviewRating() {
		return reviewRating;
	}

	public void setReviewRating(int reviewRating) {
		this.reviewRating = reviewRating;
	}

	public String getReviewPermalink() {
		return reviewPermalink;
	}

	public void setReviewPermalink(String reviewPermalink) {
		this.reviewPermalink = reviewPermalink;
	}

	public String getReviewAddDate() {
		return reviewAddDate;
	}

	public void setReviewAddDate(String reviewAddDate) {
		this.reviewAddDate = reviewAddDate;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public int getReviewForOrderId() {
		return reviewForOrderId;
	}

	public void setReviewForOrderId(int reviewForOrderId) {
		this.reviewForOrderId = reviewForOrderId;
	}

}
