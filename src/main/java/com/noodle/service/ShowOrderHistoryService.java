package com.noodle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.domain.Order;
import com.noodle.repository.OrderRepository;

/**
 * 注文履歴を表示するサービスクラス.
 * @author yuichi
 *
 */
@Service
@Transactional
public class ShowOrderHistoryService {

	@Autowired
	private OrderRepository orderRepository;
	
	/**
	 * ユーザーIDでステータス3(発送済)の注文を取得するメソッド.
	 * TODO 本当はステータス3だけ取得したいが現状代引きしか使えないのでステータス1をとる
	 * @param userId
	 * @return ステータス1の注文リスト
	 */
	public List<Order> showOrderHistory(Integer userId) {
		List<Order> orderList = orderRepository.findOrderByUserIdAndStatus(userId, 1);
		return orderList;
	}
	
}
