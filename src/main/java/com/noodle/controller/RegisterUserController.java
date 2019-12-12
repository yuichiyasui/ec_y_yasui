package com.noodle.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.form.RegisterUserForm;
import com.noodle.service.AddToCartService;
import com.noodle.service.RegisterUserService;

/**
 * ユーザー登録処理を行うコントローラクラス.
 * @author yuichi
 *
 */
@Controller
public class RegisterUserController {

	@Autowired
	private RegisterUserService registerUserService;
	
	/** ロギング処理 */
	private static final Logger LOGGER
	= LoggerFactory.getLogger(AddToCartService.class);
	
	/**
	 * エラーチェック用.
	 * @return 空のフォームオブジェクト
	 */
	@ModelAttribute
	public RegisterUserForm setUpUserRegisterForm() {
		return new RegisterUserForm();
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
	 * ユーザー登録処理を行うメソッド.
	 * @param form register_user.htmlから受け取ったリクエストパラメータ
	 * @return ログイン画面 / ユーザ登録画面
	 */
	@RequestMapping("/registerUser")
	public String registerUser(
			@Validated RegisterUserForm form,
			BindingResult result,
			Model model
			) {
		// 条件1：メールアドレスが重複していないかチェック
		if (registerUserService.findByEmail(form.getEmail()) != null) {
			result.rejectValue("email", "", "*既に登録されているメールアドレスです");
			LOGGER.info("既に登録されているメールアドレスでした");
		}
		// 条件2：パスワードが一致しているかどうかチェック
		if(!(form.getPassword().equals(form.getConfirmationPassword()))) {
			result.rejectValue("confirmationPassword","", "*パスワードが一致しません");
			LOGGER.info("パスワードの一致しませんでした");
		}
		// 条件3：上記以外にエラーがないかチェック
		if(result.hasErrors()) {
			LOGGER.info("入力項目にエラーがありました");
			return showRegisterUser();
		}
		// 条件4：何もエラーがない場合
		registerUserService.RegisterUser(form);
		LOGGER.info("ユーザー登録が完了しました");
		return "redirect:/showLogin";
	}
	
}
