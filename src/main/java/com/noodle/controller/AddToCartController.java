package com.noodle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.form.OrderItemForm;
import com.noodle.service.AddToCartService;

/**
 * カートに追加する処理を行うコントローラクラス.
 * @author yuichi
 *
 */
@Controller
public class AddToCartController {

	@Autowired
	private AddToCartService addToCartService;
	
	/**
	 * カートに追加してショピングカートを表示するメソッド.
	 * @param form item_detail.htmlから受け取ったリクエストパラメータ
	 * @param model リクエストスコープ
	 * @return ショッピングカート画面
	 */
	@RequestMapping("/addToCart")
	public String addToCart(OrderItemForm form, Model model) {		
		addToCartService.addToCart(form);
		return "forward:/showCartList";
	}
	
}
