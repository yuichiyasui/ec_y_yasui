package com.noodle.controller;

import java.math.BigInteger;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.domain.LoginUser;
import com.noodle.domain.Order;
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
	
	/** ロギング処理 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartController.class);
	
	/**
	 * カートに追加してショピングカートを表示するメソッド.
	 * @param form item_detail.htmlから受け取ったリクエストパラメータ
	 * @param model リクエストスコープ
	 * @return ショッピングカート画面
	 */
	@RequestMapping("/addToCart")
	public String addToCart(OrderItemForm form, @AuthenticationPrincipal LoginUser loginUser) {
		// 注文を作成して商品を追加して、作成した注文のユーザーIDを取得する
		Integer userId = addToCartService.addToCart(form, loginUser);
		/* ログインした際に、仮IDを基に注文情報を取得して
		 * 本IDに書き換える為にセッションスコープに格納しておく 
		 */
		session.setAttribute("userId", userId);
		return "redirect:/showCartList";
	}
	
	/**
	 * カートを表示するメソッド.
	 * @param loginUser ログインユーザー情報
	 * @param model　リクエストスコープ
	 * @return ショッピングカート画面
	 */
	@RequestMapping("/showCartList")
	public String showCartList(
			@AuthenticationPrincipal LoginUser loginUser,
			Model model
			) {
		Integer userId;
		if (loginUser != null) {
			// ログインしている場合の処理
			userId = loginUser.getUser().getId();
		} else if (session.getAttribute("userId") != null) {
			// カートに商品が追加されている(セッションスコープにuserIDが入っている)場合の処理
			userId = (Integer) (session.getAttribute("userId"));
		} else {
			// カートに商品が追加されていなくてログインしていない場合
			// 仮IDを発行
			userId = new BigInteger(session.getId(), 16).hashCode();
			/* ログインした際に、仮IDを基に注文情報を取得して
			 * 本IDに書き換える為にセッションスコープに格納しておく 
			 */
			session.setAttribute("userId", userId);
		}
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
			LOGGER.info("注文商品が空になりました");
			order = null;
		}
		if (order == null || order.getOrderItemList() == null) {
			// 注文が存在していないか、注文はあるが注文商品がない場合
			model.addAttribute("message", "カートの中身が空です。");
		} else {
			// 注文と注文商品が存在している場合
			model.addAttribute("order", order);
		}
		return "redirect:/showCartList";
	}
	
}
