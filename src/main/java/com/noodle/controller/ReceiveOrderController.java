package com.noodle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.form.ReceiveOrderForm;
import com.noodle.service.ReceiveOrderService;
import com.noodle.service.ShowCartListService;

/**
 * 注文を受けるコントローラークラス.
 * order_confirm.htmlを表示、操作する
 * @author yuichi
 *
 */
@Controller
public class ReceiveOrderController {

	@Autowired
	private ShowCartListService showCartListService;
	@Autowired
	private ReceiveOrderService receiveOrderService;
	
	/**
	 * 注文確認画面に遷移するメソッド.
	 * @return 注文確認画面
	 */
	@RequestMapping("/showOrderConfirm")
	public String showOrderConfirm(Integer userId, Model model) {
		model.addAttribute("order", showCartListService.showCartList(userId));
		return "order_confirm.html";
	}
	
	/**
	 * 注文完了画面に遷移するメソッド.
	 * @return 注文完了画面
	 */
	@RequestMapping("/showOrderFinished")
	public String showOrderFinished(ReceiveOrderForm form) {
		receiveOrderService.receiveOrder(form);
		return "order_finished.html";
	}
	
}
