package com.noodle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.repository.OrderItemRepository;

/**
 * カートの商品と付随するトッピングを削除するサービスクラス.
 * @author yuichi
 *
 */
@Service
@Transactional
public class DeleteCartItemService {

	@Autowired
	private OrderItemRepository orderItemRepository;
	
	/**
	 * 注文商品IDで注文商品とそれに関連するトッピングを削除するメソッド.
	 * @param id 注文商品ID
	 */
	public void deleteOrderItemAndOrderToppingById(Integer orderItemId) {
		orderItemRepository.deleteOrderItemAndOrderToppingById(orderItemId);
	}
}
