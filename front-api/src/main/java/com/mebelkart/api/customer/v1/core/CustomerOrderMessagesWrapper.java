/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

import java.util.Date;

import com.github.rkmk.annotations.ColumnName;
import com.github.rkmk.annotations.PrimaryKey;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerOrderMessagesWrapper {
	@PrimaryKey()
	@ColumnName("id_message")
	private int messageId;
	@ColumnName("message")
	private String message;
	@ColumnName("date_add")
	private Date dateAdded;

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getMessage() {
		if(!(message == null)){
			message = message.replace("\n", "").replace("\r", "");
		}
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

}
