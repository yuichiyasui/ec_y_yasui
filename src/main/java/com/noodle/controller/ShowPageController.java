package com.noodle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.form.OrderItemForm;
import com.noodle.form.RegisterUserForm;
import com.noodle.service.ShowItemDetailService;
import com.noodle.service.ShowItemListService;

/**
 * 各ページへ遷移するコントローラークラス.
 * @author yuichi
 *
 */
@Controller
public class ShowPageController {

	@Autowired
	private ShowItemListService showItemListService;
	@Autowired
	private ShowItemDetailService showItemDetailService;
	
	/**
	 * ユーザー登録画面のエラーチェック用.
	 * @return 空のRegisterUserFormオブジェクト
	 */
	@ModelAttribute
	public RegisterUserForm setUpRegisterUserForm() {
		return new RegisterUserForm();
	}
	
	/**
	 * 商品一覧画面に遷移するメソッド.
	 * @return 商品一覧画面
	 */
	@RequestMapping("")
	public String showItemList(Model model) {
		model.addAttribute("itemList", showItemListService.showItemList());
		return "item_list.html";
	}

	/**
	 * 商品詳細画面に遷移するメソッド.
	 * @return 商品詳細画面
	 */
	@RequestMapping("/showItemDetail")
	public String showItemDetail(Integer id, Model model) {
		model.addAttribute("item", showItemDetailService.showItemDetail(id));
		return "item_detail.html";
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
	 * ログアウト仮置き用.
	 * あとでSpringSecurityに置き換える
	 * @param model
	 * @return 商品一覧画面
	 */
	@RequestMapping("/logout")	//TODO SpringSecurityでログアウト実装したらなくす
	public String logout(Model model) {
		return showItemList(model);
	}
	
	/**
	 * 注文履歴画面に遷移するメソッド.
	 * @return 注文履歴画面
	 */
	@RequestMapping("/showOrderHistory")
	public String showOrderHistory() {
		return "order_history.html";
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
	 * ショッピングカート画面に遷移するメソッド.
	 * @return ショッピングカート画面
	 */
	@RequestMapping("/showCartList")
	public String showCartList(OrderItemForm form) {
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
