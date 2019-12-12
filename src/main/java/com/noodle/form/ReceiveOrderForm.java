package com.noodle.form;

/**
 * order_confirm.htmlからお届け先情報を受け取るフォームクラス.
 * @author yuichi
 *
 */
public class ReceiveOrderForm {
	/**	注文ID */
	private Integer id;
	/**	ユーザーID */
	private Integer userId;
	/**	注文者名 */
	private String destinationName;
	/**	注文者のメールアドレス */
	private String destinationEmail;
	/**	注文者の郵便番号 */
	private String destinationZipcode;
	/**	注文者の住所 */
	private String destinationAddress;
	/**	注文者の電話番号 */
	private String destinationTel;
	/**	配達日 */
	private String deliveryDate;
	/**	配達時間 */
	private String deliveryTime;
	/**	決済方法(数字) */
	private Integer paymentMethod;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public String getDestinationEmail() {
		return destinationEmail;
	}
	public void setDestinationEmail(String destinationEmail) {
		this.destinationEmail = destinationEmail;
	}
	public String getDestinationZipcode() {
		return destinationZipcode;
	}
	public void setDestinationZipcode(String destinationZipcode) {
		this.destinationZipcode = destinationZipcode;
	}
	public String getDestinationAddress() {
		return destinationAddress;
	}
	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}
	public String getDestinationTel() {
		return destinationTel;
	}
	public void setDestinationTel(String destinationTel) {
		this.destinationTel = destinationTel;
	}
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	public Integer getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	@Override
	public String toString() {
		return "ReceiveOrderForm [id=" + id + ", userId=" + userId + ", destinationName=" + destinationName
				+ ", destinationEmail=" + destinationEmail + ", destinationZipcode=" + destinationZipcode
				+ ", destinationAddress=" + destinationAddress + ", destinationTel=" + destinationTel
				+ ", deliveryDate=" + deliveryDate + ", deliveryTime=" + deliveryTime + ", paymentMethod="
				+ paymentMethod + "]";
	}
}
