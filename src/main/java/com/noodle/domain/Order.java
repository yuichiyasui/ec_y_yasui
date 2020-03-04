package com.noodle.domain;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 注文情報のドメインクラス.
 * @author yuichi
 *
 */
public class Order {
	/**	注文ID */
	private Integer id;
	/**	ユーザーID */
	private Integer userId;
	/**	注文ステータス(0:注文前 1:未入金 2:入金済 3:発送済 9:キャンセル) */
	private Integer status;
	/**	合計金額 */
	private Integer totalPrice;
	/**	注文日 */
	private Date orderDate;
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
	/**	配達日時 */
	private Timestamp deliveryTime;
	/**	決済方法(1:代金引換 2:クレジットカード) */
	private Integer paymentMethod;
	/**	ユーザー情報 */
	private User user;
	/**	注文商品リスト */
	private List<OrderItem> orderItemList;

	/**
	 * 合計金額から消費税を計算するメソッド.
	 * @return 消費税
	 */
	public int getTax() {
		int tax = (int)(getCalcTotalPrice() * 0.1);
		return tax;
	}
	/**
	 * 合計金額を計算するメソッド.
	 * @return 合計金額(税抜)
	 */
	public int getCalcTotalPrice() {
		totalPrice = 0;
		try {
			for(OrderItem orderItem : getOrderItemList()) {	
				totalPrice += orderItem.getSubTotal();
			}			
			return totalPrice;
		}catch (Exception e) {
			e.printStackTrace();
			return totalPrice;
		}
	}	
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
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
	public Timestamp getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(Timestamp deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	public Integer getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	@Override
	public String toString() {
		return "Order [id=" + id + ", userId=" + userId + ", status=" + status + ", totalPrice=" + totalPrice
				+ ", orderDate=" + orderDate + ", destinationName=" + destinationName + ", destinationEmail="
				+ destinationEmail + ", destinationZipcode=" + destinationZipcode + ", destinationAddress="
				+ destinationAddress + ", destinationTel=" + destinationTel + ", deliveryTime=" + deliveryTime
				+ ", paymentMethod=" + paymentMethod + ", user=" + user + ", orderItemList=" + orderItemList + "]";
	}
}
