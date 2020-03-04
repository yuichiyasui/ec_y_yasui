package com.noodle.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.domain.LoginUser;
import com.noodle.form.ReceiveOrderForm;
import com.noodle.service.ShowItemDetailService;
import com.noodle.service.ShowItemListService;
import com.noodle.service.ShowOrderHistoryService;

/**
 * 各ページへ遷移するコントローラークラス.
 * 
 * @author yuichi
 *
 */
@Controller
@SpringBootApplication
public class ShowPageController {

	@Autowired
	private ShowItemListService showItemListService;
	@Autowired
	private ShowItemDetailService showItemDetailService;
	@Autowired
	private ShowOrderHistoryService showOrderHistoryService;

	/** ロギング処理 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ShowPageController.class);

	/**
	 * お届け先情報入力画面のエラーチェック用.
	 * 
	 * @return 空のReceiveOrderFormオブジェクト
	 */
	@ModelAttribute
	public ReceiveOrderForm setUpReceiveOrderForm() {
		return new ReceiveOrderForm();
	}

	/**
	 * 商品一覧画面に遷移するメソッド.
	 * 
	 * @return 商品一覧画面
	 */
	@RequestMapping("")
	public String showItemList(Model model) {
		model.addAttribute("itemList", showItemListService.showItemList());
		return "item_list.html";
	}

	@RequestMapping("/logoutSuccess")
	public String logoutSuccess() {
		LOGGER.info("ログアウトに成功しました");
		return "forward:/";
	}

	/**
	 * 商品詳細画面に遷移するメソッド.
	 * 
	 * @return 商品詳細画面
	 */
	@RequestMapping("/showItemDetail")
	public String showItemDetail(Integer id, Model model) {
		model.addAttribute("item", showItemDetailService.showItemDetail(id));
		return "item_detail.html";
	}

	/**
	 * 注文履歴画面に遷移するメソッド.
	 * 
	 * @return 注文履歴画面
	 */
	@RequestMapping("/showOrderHistory")
	public String showOrderHistory(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		model.addAttribute("orderList", showOrderHistoryService.showOrderHistory(loginUser.getUser().getId()));
		return "order_history.html";
	}
	
	/**
	 * 会員登録解除完ページに遷移するメソッド.
	 * @return 会員登録解除完了ページ
	 */
	@RequestMapping("/thanks")
	public String thanks() {
		return "config/thanks.html";
	}

}
