package com.noodle.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.domain.LoginUser;
import com.noodle.service.LoginService;

/**
 * ログイン処理を行うコントローラークラス.
 * 
 * @author yuichi
 *
 */
@Controller
public class LoginController {

	/** URLのドメイン */
	// TODO [ec_y_yasui]デプロイ時に変更
	private final String URL_DOMAIN = "http://localhost:8080";
//	private final String URL_DOMAIN = "https://ramen-d.herokuapp.com";
	
	@Autowired
	private LoginService loginService;
	@Autowired
	private HttpSession session;
	@Autowired
	private HttpServletRequest request;

	/** ロギング処理 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	/**
	 * ログイン画面を表示するメソッド.
	 * 
	 * @return ログイン画面
	 * @throws IOException
	 * @throws ServletException
	 */
	@RequestMapping("/showLogin")
	public String showLogin() throws ServletException, IOException {
		try {
			if (request.getHeader("REFERER").equals(URL_DOMAIN + "/showRegisterUser")
					|| request.getHeader("REFERER").equals(URL_DOMAIN + "/registerUser")) {
				// ログイン画面に遷移前のページがユーザー登録画面かユーザー登録処理のパスであれば何もしない
			} else {
				// ログイン画面に遷移前のページが上記以外ならセッションスコープに遷移前のページ情報を格納
				session.setAttribute("referer", request.getHeader("REFERER"));
			}
		} catch (Exception e) {
			LOGGER.debug("存在しないパスへのアクセスがありました");
			e.printStackTrace();
		}
		return "login.html";
	}

	/**
	 * ログイン成功時に、ログイン前の仮ユーザーIDに紐づく注文情報を ログイン後のユーザーIDに紐づけるメソッド.
	 * 
	 * @param loginUser ログインユーザー情報
	 * @return ログイン画面に遷移前の画面
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/loginSuccess")
	public String loginSuccess(@AuthenticationPrincipal LoginUser loginUser) throws ServletException, IOException {
		Integer userId = loginUser.getUser().getId();
		Integer preUserId = (Integer) session.getAttribute("userId");
		if (loginService.isCheckOrderExist(userId) == false) {
			// ログインしたユーザーが注文前の注文情報を保持していない場合
			loginService.createNewOrderWhenLogin(userId);
		}
		if (preUserId != null) { // ログイン前に商品を追加していないとpreIdはnullになる
			// ログイン前に注文がある場合
			loginService.updateOrderItemsOrderId(userId, preUserId);
			loginService.deleteOrder(preUserId);
			LOGGER.info("ログイン前の注文情報をログインユーザーに移行しました");
		}
		// ログイン画面に遷移前のページのURLをパスだけ切り出す
		String pathBeforeLoginPage;
		try {
			pathBeforeLoginPage = session.getAttribute("referer").toString().replace(URL_DOMAIN, "");
		} catch (Exception e) {
			e.printStackTrace();
			return "forward:/";
		}
		if(pathBeforeLoginPage.equals("/showCartList")) {
			// ログイン前の画面がショッピングカート画面であった場合、注文確認画面に遷移させる
			pathBeforeLoginPage = "/showOrderConfirm";
			return "forward:"+ pathBeforeLoginPage + "?userId=" + userId;
		}
		// リファラ情報を削除
		session.removeAttribute("referer");
		LOGGER.info("ユーザーID:" + userId + "がログインに成功しました");
		return "forward:" + pathBeforeLoginPage;
	}

	/**
	 * ログインエラーの際、ログイン画面に遷移しエラーを表示するメソッド.
	 * 
	 * @param model リクエストスコープ
	 * @return ログイン画面
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/loginError")
	public String loginError(Model model) throws ServletException, IOException {
		model.addAttribute("error", "*メールアドレス、またはパスワードが間違っています");
		LOGGER.info("ログインの失敗がありました");
		return "login.html";
	}
}
