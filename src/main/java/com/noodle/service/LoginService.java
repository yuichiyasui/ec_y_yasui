package com.noodle.service;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

	/**
	 * ログイン前に仮ユーザーIDで作成した注文情報のユーザーIDをログイン後に本ユーザーIDに移行するメソッド.
	 * 
	 * @param userId    本ユーザーID
	 * @param preUserId 仮ユーザーID
	 */
	public void updateOrderItemsOrderId(Integer userId, Integer preUserId) {
		// 仮ユーザーIDでログイン前の注文IDを取得
		Integer preOrderId = orderRepository.findByUserIdAndStatus(preUserId, 0).getId();
		Integer orderId;
		try {
			orderId = orderRepository.findByUserIdAndStatus(userId, 0).getId();
			LOGGER.info("ユーザーID:" + userId + "は前回のログイン時の注文がありました");
		} catch (Exception e) {
			orderId = 0;
			LOGGER.info("ユーザーID:" + userId + "のカートには注文前の商品がありませんでした。");
		}
		if (orderId == 0) {
			/** 1-1: 前回ログイン時の注文がカートに残っていない場合 */
			Order order = createNewOrderWhenLogin(userId);
			LOGGER.info("注文ID:" + order.getId() + "の注文を新規に作成しました");
			orderItemRepository.updateOrderItemId(order.getId(), preOrderId);
			LOGGER.info("ログイン前に作成した注文ID:" + preOrderId + "の注文情報を注文ID:" + order.getId() + "に移行します");
		} else {
			/** 1-2: 前回ログイン時の注文がカートに残っている場合 */
			// 前のオーダーIDで検索して、今のオーダーIDに書き換え
			LOGGER.info("ログイン前に作成した注文ID:" + preOrderId + "の注文情報を前回のログイン時に作成された注文ID:" + orderId + "に移行します");
			orderItemRepository.updateOrderItemId(orderId, preOrderId);
		}
		orderRepository.deleteById(preOrderId);
		LOGGER.info("移行前の注文ID:" + preOrderId + "を削除しました");
		LOGGER.info("ユーザーID:" + session.getAttribute("userId") + "をセッションから削除します");
		session.removeAttribute("userId");
		LOGGER.info("セッションからユーザーIDを削除しました");
	}

	/**
	 * ユーザーIDで検索して注文が存在しているかどうかチェックするメソッド.
	 * 
	 * @param userId ユーザーID
	 * @return true 注文が存在している / false 注文が存在していない
	 */
	public boolean isCheckOrderExist(Integer userId) {
		if (orderRepository.findByUserIdAndStatus(userId, 0) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ログイン時に注文前の注文情報がなかった場合、 注文情報を新しく作るメソッド.
	 * 
	 * @param userId ユーザーID
	 * @return 作成した注文情報
	 */
	public Order createNewOrderWhenLogin(Integer userId) {
		Order order = new Order();
		order.setStatus(0);
		order.setTotalPrice(0);
		order.setUserId(userId);
		return orderRepository.insertWithReturnOrder(order);
	}

}
