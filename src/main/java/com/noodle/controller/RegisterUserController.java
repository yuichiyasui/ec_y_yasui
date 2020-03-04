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
@RequestMapping("/registerUser")
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
	public RegisterUserForm setUpRegisterUserForm() {
		return new RegisterUserForm();
	}
	
	/**
	 * ユーザー登録画面に遷移するメソッド.
	 * @return ユーザー登録画面
	 */
	@RequestMapping("/showRegisterUser")
	public String showRegisterUser() {
		return "register_user/register_user.html";
	}
	
	/**
	 * @return
	 */
	@RequestMapping("/mailSent")
	public String mailSent() {
		return "register_user/mail_sent.html";
	}
	
	/**
	 * 仮ユーザー登録処理を行うメソッド.
	 * @param form register_user.htmlから受け取ったリクエストパラメータ
	 * @return 登録確認メール送信完了画面 / ユーザ登録画面
	 */
	@RequestMapping("/registerPreUser")
	public String registerPreUser(
			@Validated RegisterUserForm form,
			BindingResult result,
			Model model
			) {
		// 条件1：メールアドレスが重複していないかチェック
		if (registerUserService.findByEmail(form.getEmail()) != null) {
			result.rejectValue("email", "", "*既に登録されているメールアドレスです");
			model.addAttribute("isError","*エラーがあります");
			LOGGER.info("既に登録されているメールアドレスでした");
		}
		// 条件2：パスワードが一致しているかどうかチェック
		if(!(form.getPassword().equals(form.getConfirmationPassword()))) {
			result.rejectValue("confirmationPassword","", "*パスワードが一致しません");
			model.addAttribute("isError","*エラーがあります");
			LOGGER.info("パスワードが一致しませんでした");
		}
		// 条件3：上記以外にエラーがないかチェック
		if(result.hasErrors()) {
			LOGGER.info("入力項目にエラーがありました");
			model.addAttribute("isError","*エラーがあります");
			return showRegisterUser();
		}
		// 条件4：何もエラーがない場合
		registerUserService.RegisterPreUser(form);
		LOGGER.info("仮ユーザー登録が完了しました");
		return "redirect:/registerUser/mailSent";
	}
	
	/**
	 * 本ユーザー登録処理を行うメソッド.
	 * @param id メールから受け取るUUID
	 * @return 登録完了画面へリダイレクト
	 */
	@RequestMapping("/register")
	public String register(String id) {
		registerUserService.registerUser(id);
		return "redirect:/registerUser/complete";
	}
	
	/**
	 * 登録完了画面を表示するメソッド.
	 * @return
	 */
	@RequestMapping("/complete")
	public String complete() {
		return "register_user/complete.html";
	}
	
	
}
