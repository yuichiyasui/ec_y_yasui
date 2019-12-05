package com.noodle.domain;

import java.util.List;

/**
 * 注文商品のドメインクラス.
 * @author yuichi
 *
 */
public class OrderItem {
	/**	注文商品ID */
	private Integer id;
	/**	商品ID */
	private Integer itemId;
	/**	注文ID */
	private Integer orderId;
	/**	数量 */
	private Integer quantity;
	/**	サイズ */
	private Character size;
	/**	商品情報 */
	private Item item;
	/**	注文トッピングリスト */
	private List<OrderTopping> orderToppingList;

	/**
	 * 注文商品の合計金額の計算.
	 * @return 商品の合計金額
	 */
	public int getSubTotal() {
		if(size == 'M') {
			int totalPrice = (item.getPriceM() + (orderToppingList.size() * 200)) * quantity; 
//			for(orderTopping : orderToppingList) {
//				orderTopping.getTopping().getPriceM();
//			}
			// TODO orderToppingListの中のorderToppingの中のToppingの中の
			return totalPrice;
		}else {
			int totalPrice = (item.getPriceM() + (orderToppingList.size() * 300)) * quantity; 
			return totalPrice;
		}
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Character getSize() {
		return size;
	}
	public void setSize(Character size) {
		this.size = size;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public List<OrderTopping> getOrderToppingList() {
		return orderToppingList;
	}
	public void setOrderToppingList(List<OrderTopping> orderToppingList) {
		this.orderToppingList = orderToppingList;
	}
	@Override
	public String toString() {
		return "OrderItem [id=" + id + ", itemId=" + itemId + ", orderId=" + orderId + ", quantity=" + quantity
				+ ", size=" + size + ", item=" + item + ", orderToppingList=" + orderToppingList + "]";
	}
}
