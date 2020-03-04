package com.noodle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.domain.Order;
import com.noodle.repository.OrderRepository;

/**
 * ショッピングカートの中身を表示するサービスクラス.
 * @author yuichi
 *
 */
@Service
@Transactional
public class ShowCartListService {

	@Autowired
	private OrderRepository orderRepository;
	
	/**
	 * ステータス0(注文前)の注文情報を取得するメソッド.
	 * @param userId ユーザーID
	 * @return 注文情報
	 */
	public Order showCartList(Integer userId) {
		return orderRepository.findOrderByUserIdAndStatus(userId, 0).get(0);
	}
}
