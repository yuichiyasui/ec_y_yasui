package com.noodle.service;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.common.EnvironmentsConfiguration;
import com.noodle.domain.Order;
import com.noodle.domain.OrderItem;
import com.noodle.domain.OrderTopping;
import com.noodle.domain.User;
import com.noodle.form.ReceiveOrderForm;
import com.noodle.repository.OrderRepository;
import com.noodle.repository.UserRepository;

/**
 * order_confirm.htmlからお届け先情報を受け取り、 注文受領処理を行うサービスクラス.
 * 
 * @author yuichi
 *
 */
@Service
@Transactional
public class ReceiveOrderService {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EnvironmentsConfiguration envConfig;
	@Autowired
	private MailSender sender;

	/** ロギング処理 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AddToCartService.class);

	/**
	 * ユーザーIDを基にユーザー情報を取得するメソッド.
	 * 
	 * @param userId ユーザーID
	 * @return ユーザー情報
	 */
	public User getUserInfomationById(Integer userId) {
		return userRepository.load(userId);
	}

	/**
	 * 注文受領処理を行うメソッド.
	 * 
	 * @param form お届け先情報
	 */
	public void receiveOrder(ReceiveOrderForm form) {
		Order order = new Order();
		order = orderRepository.load(form.getId());
		if (form.getPaymentMethod() == 1) {
			// 決済方法が代引き(1)の場合→未入金(1)
			order.setStatus(1);
			LOGGER.info("決済方法は代引きが選択されました");
		} else {
			// 決済方法がクレジットカード(2)の場合→入金済み(2)
			order.setStatus(2);
			LOGGER.info("決済方法はクレジットカードが選択されました");
		}
		order.setTotalPrice(order.getCalcTotalPrice()+order.getTax());
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
		LocalDateTime dDateTime = LocalDateTime.of(dDate.getYear(), dDate.getMonth(), dDate.getDayOfMonth(),
				Integer.parseInt(form.getDeliveryTime()), 00);
		// LocalDateTime型をTimestamp型に変換する
		Timestamp deliveryTime = Timestamp.valueOf(dDateTime);
		order.setDeliveryTime(deliveryTime);
		order.setPaymentMethod(form.getPaymentMethod());
		orderRepository.update(order);
		/** メール送信 */
		sendOrderFinishMail(order);
	}

	/**
	 * 注文完了メールを送信するメソッド.
	 * @param order 注文情報
	 */
	public void sendOrderFinishMail(Order order) {
		User user = userRepository.load(order.getUserId());
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(envConfig.getMail());
		msg.setTo(order.getDestinationEmail());
		msg.setSubject("[らくらく軒]ラーメンのご注文を承りました");
		StringBuilder str = new StringBuilder();
		NumberFormat nfCur = NumberFormat.getCurrencyInstance();
		str.append(user.getName());
		str.append(" 様\n\nらくらく軒をご利用いただきまして誠にありがとうございます。\n");
		str.append("ご注文を承りましたことを本メールにて通知いたします。\n\n");
		str.append("【ご注文内容】---------------------------------------\n\n");
		for (OrderItem orderItem : order.getOrderItemList()) {
			str.append(orderItem.getItem().getName() + "\n");
			char size = orderItem.getSize();
			str.append(size + "サイズ ");
			int price;
			if (size == 'M') {
				price = orderItem.getItem().getPriceM();
			} else {
				price = orderItem.getItem().getPriceL();
			}
			str.append(nfCur.format(price) + " ");
			str.append(orderItem.getQuantity() + "個\nトッピング：");
			if (orderItem.getOrderToppingList().get(0).getTopping().getName() == null) {
				// トッピングがない場合
				str.append("なし");
			} else {
				// トッピングある場合
				for (OrderTopping orderTopping : orderItem.getOrderToppingList()) {
					str.append(orderTopping.getTopping().getName());
					if (size == 'M') {
						str.append("(" + nfCur.format(orderTopping.getTopping().getPriceM()) + ") ");
					} else {
						str.append("(" + nfCur.format(orderTopping.getTopping().getPriceL()) + ") ");
					}
				}
			}
			str.append("\n商品小計  ：");
			str.append(nfCur.format(orderItem.getSubTotal()) + "\n\n");
		}
		str.append("【お支払い金額】--------------------------------------\n\nご注文小計　　：");
		str.append(nfCur.format(order.getCalcTotalPrice()));
		str.append("\n消費税　　　　：");
		str.append(nfCur.format(order.getTax()));
		str.append("\n合計金額(税込) ：");
		str.append(nfCur.format(order.getCalcTotalPrice() + order.getTax()));
		str.append("\n\n【詳細】------------------------------------------------\n\n注文者：");
		str.append(order.getDestinationName() + " 様");
		str.append("\n支払い方法：");
		if (order.getPaymentMethod() == 1) {
			str.append("代金引換\n配達先：");
		} else if (order.getPaymentMethod() == 2) {
			str.append("クレジットカード決済\n配達先：");
		}
		str.append(order.getDestinationAddress());
		str.append("\n配達日時：");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		str.append(sdf.format(order.getDeliveryTime()));
		str.append("\n");
		str.append(
				"\n-----------------------------------------------------------\n\n以上でございます。\n \n配達予定のお時間にはご在宅いただき、\n商品をお受け取りいただくようお願い申し上げます。\n\n改めてこの度はご注文いただき誠にありがとうございます。\n今後ともらくらく軒をどうぞよろしくお願いいたします。\n\n\n卍----------------------------------------------------------卍\n\n　NOODLE DELIVERY SHOP - らくらく軒 -\n　" + envConfig.getUrl() +"\n\n卍----------------------------------------------------------卍");
		msg.setText(str.toString());
		try {
			this.sender.send(msg);
			LOGGER.info(user.getEmail() + "へのメールの送信に成功しました");
		}catch(Exception e) {
			e.printStackTrace();
			LOGGER.warn(user.getEmail() + "へのメールの送信に失敗しました");
			
		}
	}

}
