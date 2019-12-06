package com.noodle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * カートに追加する処理を行うコントローラクラス.
 * @author yuichi
 *
 */
@Controller
public class AddToCartController {


	@RequestMapping("/addToCart")
	public String addToCart(Model model) {
		
		return "/showCartList";
	}
	
}
