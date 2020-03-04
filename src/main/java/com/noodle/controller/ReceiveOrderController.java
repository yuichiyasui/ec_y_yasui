package com.noodle.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.domain.LoginUser;
import com.noodle.domain.Order;
import com.noodle.form.ReceiveOrderForm;
import com.noodle.service.DeleteCartItemService;
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
	@Autowired
	private DeleteCartItemService deleteCartItemService;

	private final static Logger LOGGER = LoggerFactory.getLogger(ReceiveOrderController.class);

	
	
	/**
	 * 注文確認画面に遷移するメソッド.
	 * 
	 * @return 注文確認画面
	 */
	@RequestMapping("/showOrderConfirm")
	public String showOrderConfirm(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		Integer userId = loginUser.getUser().getId();
		Order order;
		try {
			order = showCartListService.showCartList(userId);
		} catch (Exception e) {
			LOGGER.info("注文商品が空になりました");
			order = null;
		}
		if (order == null || order.getOrderItemList() == null) {
			// 注文が存在していないか、注文はあるが注文商品がない場合
			model.addAttribute("message", "カートの中身が空です");
		} else {
			// 注文と注文商品が存在している場合
			model.addAttribute("order", order);
		}
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
		if (result.hasErrors()) {
			return "order_confirm.html";
		}
		receiveOrderService.receiveOrder(form);
		return "order_finished.html";
	}

	/**
	 * カートの商品を削除して注文確認画面を表示するメソッド.
	 * 
	 * @param id 注文商品ID
	 * @return 注文確認画面
	 */
	@RequestMapping("/deleteOrderItem")
	public String deleteItem(Integer orderItemId, Model model) {
		deleteCartItemService.deleteOrderItemAndOrderToppingById(orderItemId);
		LOGGER.info("注文商品ID:" + orderItemId + "を削除しました");
		return "redirect:/showOrderConfirm";
	}

}
