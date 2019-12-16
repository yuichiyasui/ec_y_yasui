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
		if(request.getHeader("REFERER").equals("http://localhost:8080/showRegisterUser") 
				|| request.getHeader("REFERER").equals("http://localhost:8080/registerUser")) {
		}else {
			session.setAttribute("referer", request.getHeader("REFERER"));
		}
		return "login.html";
	}
	
	@RequestMapping("/loginSuccess")
	public String loginSuccess(@AuthenticationPrincipal LoginUser loginUser) throws ServletException, IOException {
		//TODO ユーザーIDを書き換える処理を書く
		System.err.println("ログインユーザーのID:" + loginUser.getUser().getId());
		// ログイン前に商品を追加していないとpreIdはnullになる
		Integer userId = loginUser.getUser().getId();
		Integer preId = (Integer) session.getAttribute("preId");
		System.err.println("セッションID:" + preId);
		if(preId != null) {
			loginService.updateOrdersUserId(userId, preId);
			LOGGER.info("ログイン前の注文情報をログインユーザーに移行しました");
		}
		return "forward:/";
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
		model.addAttribute("error", "メールアドレス、またはパスワードが間違っています");
		return "login.html";
	}
	
	
}
