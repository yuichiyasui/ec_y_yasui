package com.noodle.service;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.domain.Order;
import com.noodle.repository.OrderItemRepository;
import com.noodle.repository.OrderRepository;

/**
 * ログイン後に仮ユーザーIDで作成した注文情報を本ユーザーIDに書き換えるサービスクラス.
 * 
 * @author yuichi
 *
 */
@Service
@Transactional
public class LoginService {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderItemRepository orderItemRepository;
	@Autowired
	private HttpSession session;

	/**
	 * ユーザーIDで検索して注文が存在しているかどうかチェックするメソッド.
	 * @param userId ユーザーID
	 * @return true 注文が存在している / false 注文が存在していない
	 */
	public boolean isCheckOrderExist(Integer userId) {
		if (orderRepository.findByUserIdAndStatus(userId) != null) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * ログイン時に注文前の注文情報がなかった場合、
	 * 注文情報を新しく作るメソッド.
	 * @param userId ユーザーID
	 */
	public void createNewOrderWhenLogin(Integer userId) {
		Order order = new Order();
		order.setStatus(0);
		order.setTotalPrice(0);
		order.setUserId(userId);
		orderRepository.insert(order);
	}
	
	/**
	 * ログイン後に仮ユーザーIDで作成した注文情報のユーザーIDを
	 * 本ユーザーIDに書き換えるメソッド.
	 * @param userId 本ユーザーID
	 * @param preUserId  仮ユーザーID
	 */
	public void updateOrderItemsOrderId(Integer userId, Integer preUserId) {
		// 仮ユーザーIDでログイン前の注文IDを取得
		Integer preOrderId = orderRepository.findIdByUserIdAndStatus(preUserId);
		// 本ユーザーIDで、本ユーザーIDに紐づく注文IDを取得
		Integer orderId = orderRepository.findIdByUserIdAndStatus(userId);
		if(orderId == 0) {
			/** ログイン前に注文をしていなかった場合 */
		}else {
			// 前のオーダーIDで検索して、今のオーダーIDに書き換え
			orderItemRepository.updateOrderItemId(orderId, preOrderId);			
		}
		session.removeAttribute("userId");
	}
	
	/**
	 * 仮ユーザーIDでログイン前の注文を削除するメソッド.
	 * @param preUserId 仮ユーザーID
	 */
	public void deleteOrder(Integer preUserId) {
		orderRepository.deleteByUserId(preUserId);
	}
	
}
