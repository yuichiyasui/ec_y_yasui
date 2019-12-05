package com.noodle.domain;

/**
 * 注文トッピングのドメインクラス.
 * @author yuichi
 *
 */
public class OrderTopping {
	/**	注文トッピングID */
	private Integer id;
	/**	トッピングID */
	private Integer toppingId;
	/**	注文商品ID */
	private Integer orderItemId;
	/**	トッピング内容 */
	private Topping topping;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getToppingId() {
		return toppingId;
	}
	public void setToppingId(Integer toppingId) {
		this.toppingId = toppingId;
	}
	public Integer getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}
	public Topping getTopping() {
		return topping;
	}
	public void setTopping(Topping topping) {
		this.topping = topping;
	}
	@Override
	public String toString() {
		return "OrderTopping [id=" + id + ", toppingId=" + toppingId + ", orderItemId=" + orderItemId + ", topping="
				+ topping + "]";
	}
}
