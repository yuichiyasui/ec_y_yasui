package com.noodle.controller;

import java.math.BigInteger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.domain.Order;
import com.noodle.domain.LoginUser;
import com.noodle.form.OrderItemForm;
import com.noodle.service.AddToCartService;
import com.noodle.service.DeleteCartItemService;
import com.noodle.service.ShowCartListService;

/**
 * カートを表示する、商品を追加する、削除する処理を行うコントローラクラス.
 * @author yuichi
 *
 */
@Controller
public class ShoppingCartController {

	@Autowired
	private ShowCartListService showCartListService;
	@Autowired
	private AddToCartService addToCartService;
	@Autowired
	private DeleteCartItemService deleteCartItemService;
	@Autowired
	private HttpSession session;
	
	/**
	 * カートに追加してショピングカートを表示するメソッド.
	 * @param form item_detail.htmlから受け取ったリクエストパラメータ
	 * @param model リクエストスコープ
	 * @return ショッピングカート画面
	 */
	@RequestMapping("/addToCart")
	public String addToCart(OrderItemForm form, Model model) {
		// 注文を作成して商品を追加して、作成した注文のユーザーIDを取得する
		Integer userId = addToCartService.addToCart(form);
		// ユーザーIDを基に注文情報を取得してリクエストスコープに格納する
		model.addAttribute("order", showCartListService.showCartList(userId));
		// ログインした際に、仮IDを基に注文情報を取得して本IDに書き換える為に
		// セッションスコープに格納しておく 
		session.setAttribute("preId", userId);
		return "cart_list.html";
	}
	
	/**
	 * カートを表示するメソッド.
	 * @param userId ユーザーID
	 * @param model リクエストスコープ
	 * @return ショッピングカート画面
	 */
	@RequestMapping("/showCartList")
	public String showCartList(
			Integer userId,
			@AuthenticationPrincipal LoginUser loginUser,
			Model model
			) {
		if (loginUser != null) {
			// ログインしている場合の処理
			userId = loginUser.getUser().getId();
		} else if (session.getAttribute("userId") != null) {
			// セッションスコープにuserIDが入っている場合の処理??
			userId = (Integer) (session.getAttribute("userId"));
		} else {
			// ログインしていない場合
			userId = new BigInteger(session.getId(), 16).intValue();
			session.setAttribute("userId", userId);
		}
		Order order;
		try {
			order = showCartListService.showCartList(userId);
		} catch (Exception e) {
			e.printStackTrace();
			order = null;
		}
		if (order == null || order.getOrderItemList() == null) {
			// 注文が存在していないか、注文はあるが注文商品がない場合
			model.addAttribute("message", "カートの中身が空です。");
		} else {
			// 注文と注文商品が存在している場合
			model.addAttribute("order", order);
		}
		return "cart_list.html";
	}
	
	/**
	 * カートの商品を削除してショッピングカートを表示するメソッド.
	 * @param id 注文商品ID
	 * @return ショッピングカート画面
	 */
	@RequestMapping("/deleteCartItem")
	public String deleteItem(Integer userId,Integer orderItemId,
			Model model) {
		// 削除処理
		deleteCartItemService.deleteOrderItemAndOrderToppingById(orderItemId);
		// 表示処理
		Order order;
		try {
			order = showCartListService.showCartList(userId);
		} catch (Exception e) {
			e.printStackTrace();
			order = null;
		}
		if (order == null || order.getOrderItemList() == null) {
			// 注文が存在していないか、注文はあるが注文商品がない場合
			model.addAttribute("message", "カートの中身が空です。");
		} else {
			// 注文と注文商品が存在している場合
			model.addAttribute("order", order);
		}
		return "cart_list.html";
	}
	
}
