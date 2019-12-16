package com.noodle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.repository.OrderRepository;

/**
 * ログイン後に仮IDで作成した注文情報を本IDに書き換えるサービスクラス.
 * @author yuichi
 *
 */
@Service
@Transactional
public class LoginService {

	@Autowired
	private OrderRepository orderRepository;
	
	/**
	 * ログイン後に仮IDで作成した注文情報のユーザーIDを本IDに書き換えるメソッド.
	 * @param userId 本ID
	 * @param preId 仮ID
	 */
	public void updateOrdersUserId(Integer userId, Integer preId) {
		orderRepository.updateOrdersUserId(userId, preId);
	}
}
