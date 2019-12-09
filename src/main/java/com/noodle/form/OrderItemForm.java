package com.noodle.form;

import java.util.List;

/**
 * item_detail.htmlから注文商品情報のパラメータを受け取るフォームクラス.
 * 
 * @author yuichi
 *
 */
public class OrderItemForm {
	/**	商品ID */
	private Integer itemId;
	/**	サイズ */
	private Character size;
	/**	数量 */
	private Integer quantity;
	/**
	 * 注文トッピングリスト.
	 * チェックボックスで選択されたtoppingIdだけ格納する
	 */
	private List<Integer> orderToppingList;

	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public Character getSize() {
		return size;
	}
	public void setSize(Character size) {
		this.size = size;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public List<Integer> getOrderToppingList() {
		return orderToppingList;
	}
	public void setOrderToppingList(List<Integer> orderToppingList) {
		this.orderToppingList = orderToppingList;
	}
	@Override
	public String toString() {
		return "OrderItemForm [itemId=" + itemId + ", size=" + size + ", quantity=" + quantity + ", orderToppingList="
				+ orderToppingList + "]";
	}
}
