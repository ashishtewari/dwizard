package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailSellerStatuses {

	private Integer orderDetailStatusId;
	private String statusName;
	private String statusDescription;
	private String statusNameDisplayToSeller;
	private String statusNameDisplayToCustomer;
	private boolean displayToSeller;
	private Integer displayToCustomer;
	private boolean sellerEditEnabled;
	private boolean sendMailToSeller;
	private boolean sendMailFromSeller;
	private boolean sendMailToCustomer;
	private String mailTemplateSeller;
	private String mailTemplateFromSeller;
	private String mailTemplateCustomer;
	private String mailSubject;
	private boolean sendSmsToSeller;
	private boolean sendSmsFromSeller;
	private boolean sendSmsToCustomer;
	private Integer statusIcon;
	private Integer position;
	private boolean active;
	
	


	public OrderDetailSellerStatuses(Integer orderDetailStatusId,
			String statusName, String statusDescription,
			String statusNameDisplayToSeller,
			String statusNameDisplayToCustomer, boolean displayToSeller,
			Integer displayToCustomer, boolean sellerEditEnabled,
			boolean sendMailToSeller, boolean sendMailFromSeller,
			boolean sendMailToCustomer, String mailTemplateSeller,
			String mailTemplateFromSeller, String mailTemplateCustomer,
			String mailSubject, boolean sendSmsToSeller,
			boolean sendSmsFromSeller, boolean sendSmsToCustomer,
			Integer statusIcon, Integer position, boolean active) {
		this.orderDetailStatusId = orderDetailStatusId;
		this.statusName = statusName;
		this.statusDescription = statusDescription;
		this.statusNameDisplayToSeller = statusNameDisplayToSeller;
		this.statusNameDisplayToCustomer = statusNameDisplayToCustomer;
		this.displayToSeller = displayToSeller;
		this.displayToCustomer = displayToCustomer;
		this.sellerEditEnabled = sellerEditEnabled;
		this.sendMailToSeller = sendMailToSeller;
		this.sendMailFromSeller = sendMailFromSeller;
		this.sendMailToCustomer = sendMailToCustomer;
		this.mailTemplateSeller = mailTemplateSeller;
		this.mailTemplateFromSeller = mailTemplateFromSeller;
		this.mailTemplateCustomer = mailTemplateCustomer;
		this.mailSubject = mailSubject;
		this.sendSmsToSeller = sendSmsToSeller;
		this.sendSmsFromSeller = sendSmsFromSeller;
		this.sendSmsToCustomer = sendSmsToCustomer;
		this.statusIcon = statusIcon;
		this.position = position;
		this.active = active;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public String getStatusNameDisplayToSeller() {
		return statusNameDisplayToSeller;
	}

	public void setStatusNameDisplayToSeller(String statusNameDisplayToSeller) {
		this.statusNameDisplayToSeller = statusNameDisplayToSeller;
	}

	public String getStatusNameDisplayToCustomer() {
		return statusNameDisplayToCustomer;
	}

	public void setStatusNameDisplayToCustomer(
			String statusNameDisplayToCustomer) {
		this.statusNameDisplayToCustomer = statusNameDisplayToCustomer;
	}

	public boolean isDisplayToSeller() {
		return displayToSeller;
	}

	public void setDisplayToSeller(boolean displayToSeller) {
		this.displayToSeller = displayToSeller;
	}

	public Integer getDisplayToCustomer() {
		return displayToCustomer;
	}

	public void setDisplayToCustomer(Integer displayToCustomer) {
		this.displayToCustomer = displayToCustomer;
	}

	public boolean isSellerEditEnabled() {
		return sellerEditEnabled;
	}

	public void setSellerEditEnabled(boolean sellerEditEnabled) {
		this.sellerEditEnabled = sellerEditEnabled;
	}

	public boolean isSendMailToSeller() {
		return sendMailToSeller;
	}

	public void setSendMailToSeller(boolean sendMailToSeller) {
		this.sendMailToSeller = sendMailToSeller;
	}

	public boolean isSendMailFromSeller() {
		return sendMailFromSeller;
	}

	public void setSendMailFromSeller(boolean sendMailFromSeller) {
		this.sendMailFromSeller = sendMailFromSeller;
	}

	public boolean isSendMailToCustomer() {
		return sendMailToCustomer;
	}

	public void setSendMailToCustomer(boolean sendMailToCustomer) {
		this.sendMailToCustomer = sendMailToCustomer;
	}

	public String getMailTemplateSeller() {
		return mailTemplateSeller;
	}

	public void setMailTemplateSeller(String mailTemplateSeller) {
		this.mailTemplateSeller = mailTemplateSeller;
	}

	public String getMailTemplateFromSeller() {
		return mailTemplateFromSeller;
	}

	public void setMailTemplateFromSeller(String mailTemplateFromSeller) {
		this.mailTemplateFromSeller = mailTemplateFromSeller;
	}

	public String getMailTemplateCustomer() {
		return mailTemplateCustomer;
	}

	public void setMailTemplateCustomer(String mailTemplateCustomer) {
		this.mailTemplateCustomer = mailTemplateCustomer;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public boolean isSendSmsToSeller() {
		return sendSmsToSeller;
	}

	public void setSendSmsToSeller(boolean sendSmsToSeller) {
		this.sendSmsToSeller = sendSmsToSeller;
	}

	public boolean isSendSmsFromSeller() {
		return sendSmsFromSeller;
	}

	public void setSendSmsFromSeller(boolean sendSmsFromSeller) {
		this.sendSmsFromSeller = sendSmsFromSeller;
	}

	public boolean isSendSmsToCustomer() {
		return sendSmsToCustomer;
	}

	public void setSendSmsToCustomer(boolean sendSmsToCustomer) {
		this.sendSmsToCustomer = sendSmsToCustomer;
	}

	public Integer getStatusIcon() {
		return statusIcon;
	}

	public void setStatusIcon(Integer statusIcon) {
		this.statusIcon = statusIcon;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getOrderDetailStatusId() {
		return orderDetailStatusId;
	}

	public void setOrderDetailStatusId(Integer orderDetailStatusId) {
		this.orderDetailStatusId = orderDetailStatusId;
	}

}
