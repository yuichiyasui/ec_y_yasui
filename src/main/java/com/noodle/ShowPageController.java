package com.noodle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 各ページへ遷移するコントローラークラス.
 * @author yuichi
 *
 */
@Controller
public class ShowPageController {

	/**
	 * 商品一覧画面に遷移するメソッド.
	 * @return 商品一覧画面
	 */
	@RequestMapping("")
	public String showItemList() {
		return "item_list.html";
	}
	
	/**
	 * ログイン画面に遷移するメソッド.
	 * @return ログイン画面
	 */
	@RequestMapping("/showLogin")
	public String showLogin() {
		return "login.html";
	}
	
	/**
	 * ユーザー登録画面に遷移するメソッド.
	 * @return ユーザー登録画面
	 */
	@RequestMapping("/showRegisterUser")
	public String showRegisterUser() {
		return "register_user.html";
	}
	
	/**
	 * 商品詳細画面に遷移するメソッド.
	 * @return 商品詳細画面
	 */
	@RequestMapping("/showItemDetail")
	public String showItemDetail() {
		return "item_detail.html";
	}
	
	/**
	 * ショッピングカート画面に遷移するメソッド.
	 * @return ショッピングカート画面
	 */
	@RequestMapping("/showCartList")
	public String showCartList() {
		return "cart_list.html";
	}
	
	/**
	 * 注文確認画面に遷移するメソッド.
	 * @return 注文確認画面
	 */
	@RequestMapping("/showOrderConfirm")
	public String showOrderConfirm() {
		return "order_confirm.html";
	}
	
	/**
	 * 注文完了画面に遷移するメソッド.
	 * @return 注文完了画面
	 */
	@RequestMapping("/showOrderFinished")
	public String showOrderFinished() {
		return "order_finished.html";
	}
}
