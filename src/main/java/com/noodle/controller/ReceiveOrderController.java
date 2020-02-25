package com.noodle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.form.ReceiveOrderForm;
import com.noodle.service.ReceiveOrderService;
import com.noodle.service.ShowCartListService;

/**
 * 注文を受けるコントローラークラス. order_confirm.htmlを表示、操作する
 * 
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
	 * 
	 * @return 注文確認画面
	 */
	@RequestMapping("/showOrderConfirm")
	public String showOrderConfirm(Integer userId, Model model) {
		model.addAttribute("order", showCartListService.showCartList(userId));
		// 入力フォームの初期値にユーザー情報をセット
		model.addAttribute("user", receiveOrderService.getUserInfomationById(userId));
		return "order_confirm.html";
	}

	@ModelAttribute
	public ReceiveOrderForm setUpReceiveOrderForm() {
		return new ReceiveOrderForm();
	}

	/**
	 * 注文完了画面に遷移するメソッド.
	 * 
	 * @return 注文完了画面
	 */
	@RequestMapping("/showOrderFinished")
	public String showOrderFinished(@Validated ReceiveOrderForm form, BindingResult result, Model model) {
		if(result.hasErrors()) {
			return "order_confirm.html";
		}
		receiveOrderService.receiveOrder(form);
		return "order_finished.html";
	}
}
