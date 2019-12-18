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
import com.noodle.service.AddToCartService;
import com.noodle.service.LoginService;

/**
 * ログイン処理を行うコントローラークラス.
 * @author yuichi
 *
 */
@Controller
public class LoginController {

	/**	URLのドメイン */
	private final String URL_DOMAIN = "http://localhost:8080";

	@Autowired
	private LoginService loginService;
	@Autowired
	private HttpSession session;
	@Autowired
	private HttpServletRequest request;
	
	/** ロギング処理 */
	private static final Logger LOGGER
	= LoggerFactory.getLogger(AddToCartService.class);
	
	/**
	 * ログイン画面を表示するメソッド.
	 * @return ログイン画面
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/showLogin")
	public String showLogin() throws ServletException, IOException {
		if(request.getHeader("REFERER").equals(URL_DOMAIN + "/showRegisterUser") 
				|| request.getHeader("REFERER").equals(URL_DOMAIN + "/registerUser")) {
			// ログイン画面に遷移前のページがユーザー登録画面かユーザー登録処理のパスであれば何もしない
		}else {
			// ログイン画面に遷移前のページが上記以外ならセッションスコープに遷移前のページ情報を格納
			session.setAttribute("referer", request.getHeader("REFERER"));
		}
		return "login.html";
	}
	
	@RequestMapping("/loginSuccess")
	public String loginSuccess(@AuthenticationPrincipal LoginUser loginUser) throws ServletException, IOException {
		// ログイン前に商品を追加していないとpreIdはnullになる
		Integer userId = loginUser.getUser().getId();
		Integer preId = (Integer) session.getAttribute("userId");
		//TODO あとで消す
		System.err.println("/loginSuccessのpreId:"+preId);
		if(preId != null) {
			loginService.updateOrdersUserId(userId, preId);
			LOGGER.info("ログイン前の注文情報をログインユーザーに移行しました");
		}
		// ログイン画面に遷移前のページのURLをパスだけ切り出す
		String pathBeforeLoginPage = session.getAttribute("referer").toString().replace(URL_DOMAIN, "");
		// リファラ情報を削除
		session.removeAttribute("referer");
		LOGGER.info("ユーザーID:" + userId + "がログインに成功しました");
		return "forward:/" + pathBeforeLoginPage;
	}
	
	/**
	 * ログインエラーの際、ログイン画面に遷移しエラーを表示するメソッド.
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
