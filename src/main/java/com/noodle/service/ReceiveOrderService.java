package com.noodle.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.domain.Order;
import com.noodle.form.ReceiveOrderForm;
import com.noodle.repository.OrderRepository;

/**
 * order_confirm.htmlからお届け先情報を受け取り、
 * 注文受領処理を行うサービスクラス.
 * @author yuichi
 *
 */
@Service
@Transactional
public class ReceiveOrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	/** ロギング処理 */
	private static final Logger LOGGER
	= LoggerFactory.getLogger(AddToCartService.class);
	
	/**
	 * 注文受領処理を行うメソッド.
	 * @param form お届け先情報
	 */
	public void receiveOrder(ReceiveOrderForm form) {
		Order order = new Order();
		order = orderRepository.load(form.getId());
		if(form.getPaymentMethod() == 1) {
			// 決済方法が代引き(1)の場合→未入金(1)
			order.setStatus(1);
			LOGGER.info("決済方法は代引きが選択されました");
		}else {
			// 決済方法がクレジットカード(2)の場合→入金済み(2)
			order.setStatus(2);
			LOGGER.info("決済方法はクレジットカードが選択されました");
		}
		// 現在の日付を取得して注文日をセット
		LocalDateTime nowDateTime = LocalDateTime.now();
		Timestamp orderDate = Timestamp.valueOf(nowDateTime);
		order.setOrderDate(orderDate);
		order.setDestinationName(form.getDestinationName());
		order.setDestinationEmail(form.getDestinationEmail());
		order.setDestinationZipcode(form.getDestinationZipcode());
		order.setDestinationAddress(form.getDestinationAddress());
		order.setDestinationTel(form.getDestinationTel());
		// String型のdeliveryDateをLocalDate型に変換
		LocalDate dDate = LocalDate.parse(form.getDeliveryDate());
		// LocalDate型をDeliveryTimeを追加してLocalDateTime型に変換
		LocalDateTime dDateTime = LocalDateTime
				.of(dDate.getYear(), dDate.getMonth(), 
						dDate.getDayOfMonth(), Integer.parseInt(form.getDeliveryTime()), 00);
		// LocalDateTime型をTimestamp型に変換する
		Timestamp deliveryTime = Timestamp.valueOf(dDateTime);
		order.setDeliveryTime(deliveryTime);
		order.setPaymentMethod(form.getPaymentMethod());
		orderRepository.update(order);
	}
	
}
