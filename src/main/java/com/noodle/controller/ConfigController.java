package com.noodle.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.noodle.domain.LoginUser;
import com.noodle.domain.User;
import com.noodle.form.ChangeInfoForm;
import com.noodle.form.ResetPasswordForm;
import com.noodle.service.ChangeInfoService;
import com.noodle.service.ReceiveContactService;
import com.noodle.service.ResetPasswordService;
import com.noodle.service.WithdrawService;

/**
 * マイページの各機能を操作するコントローラクラス.
 * 
 * @author yuichi
 *
 */
@Controller
@RequestMapping("/config")
public class ConfigController {

	@Autowired
	private ChangeInfoService changeInfoService;
	@Autowired
	private ReceiveContactService receiveContactService;
	@Autowired
	private ResetPasswordService resetPasswordService;
	@Autowired
	private WithdrawService withDrawService;
	@Autowired
	private HttpServletRequest httpServletRequest;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

	@ModelAttribute
	public ChangeInfoForm setUpChangeInfoForm() {
		return new ChangeInfoForm();
	}

	/**
	 * 設定画面に遷移するメソッド
	 * 
	 * @param loginUser
	 * @param model
	 * @return
	 */
	@RequestMapping("")
	public String showConfig(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		return "config/config.html";
	}

	/**
	 * 登録情報の変更ページに遷移するメソッド.
	 * 
	 * @param loginUser ログインユーザー情報
	 * @param model     リクエストスコープ
	 * @return
	 */
	@RequestMapping("/showChangeInfo")
	public String showChangeInfo(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		User user = loginUser.getUser();
		model.addAttribute("user", user);
		return "config/change_info.html";
	}

	/**
	 * 登録情報を変更するメソッド.
	 * 
	 * @param loginUser          ログインユーザー情報
	 * @param form               フォームから受け取ったリクエストパラメータ
	 * @param result             エラーチェック結果
	 * @param model              リクエストスコープ
	 * @param redirectAttributes フラッシュスコープ
	 * @return 登録情報変更入力画面
	 */
	@RequestMapping("/changeInfo")
	public String changeInfo(@AuthenticationPrincipal LoginUser loginUser, @Validated ChangeInfoForm form,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			LOGGER.info("入力値にエラーがあります");
			model.addAttribute("fail", "変更内容に不備があります");
			return "forward:/config/showChangeInfo";
		}
		changeInfoService.changeInfo(form, loginUser);
		redirectAttributes.addFlashAttribute("success", "登録情報の変更が完了しました");
		return "redirect:/config/showChangeInfo";
	}

	/**
	 * パスワード再設定のメール送信画面へ遷移するメソッド.
	 * 
	 * @param loginUser ログインユーザー情報
	 * @param model     リクエストスコープ
	 * @return メール送信画面
	 */
	@RequestMapping("/showChangePassword")
	public String showChangePassword(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		return "config/change_password.html";
	}

	/**
	 * パスワード再設定用のメールを送信するメソッド.
	 * 
	 * @param loginUser          ログインユーザー情報
	 * @param redirectAttributes フラッシュスコープ
	 * @return メール送信画面へリダイレクト
	 */
	@RequestMapping("/sendResetMail")
	public String sendResetMail(@AuthenticationPrincipal LoginUser loginUser, RedirectAttributes redirectAttributes) {
		resetPasswordService.sendResetMail(loginUser.getUser());
		redirectAttributes.addFlashAttribute("isSent", "送信完了");
		return "redirect:/config/showChangePassword";
	}

	/**
	 * パスワードの再設定画面に遷移するメソッド.
	 * 
	 * @param uuId  UUID
	 * @param model リクエストスコープ
	 * @return パスワード再設定画面
	 */
	@RequestMapping("/showResetPassword")
	public String showResetPassword(String uuId, Model model) {
		Integer userId = resetPasswordService.getUserByUuId(uuId);
		model.addAttribute("id", userId);
		return "config/reset_password.html";
	}

	@ModelAttribute
	public ResetPasswordForm setUpResetPasswordForm() {
		return new ResetPasswordForm();
	}

	/**
	 * パスワードの再設定を行うメソッド.
	 * 
	 * @param form   リクエストパラメータ
	 * @param result エラーチェック結果
	 * @param model  リクエストスコープ
	 * @return パスワード再設定完了画面
	 */
	@RequestMapping("/resetPassword")
	public String resetPassword(@Validated ResetPasswordForm form, BindingResult result, Model model) {
		if (!form.getPassword().equals(form.getConfirmPassword())) {
			result.rejectValue("password", "", "*パスワードが一致しません");
		}
		if (result.hasErrors()) {
			return "config/reset_password.html";
		}
		resetPasswordService.updatePasswordAndUuId(form.getId(), form.getPassword());
		return "redirect:/config/showPwdFinish";
	}

	/**
	 * パスワード再設定完了画面に遷移するメソッド.
	 * 
	 * @return パスワード再設定画面
	 */
	@RequestMapping("/showPwdFinish")
	public String showPwdFinish() {
		return "config/pwd_finish.html";
	}

	/**
	 * お問合せフォームを表示するメソッド.
	 * 
	 * @param loginUser ログインユーザー情報
	 * @param model     リクエストスコープ
	 * @return お問合せ画面
	 */
	@RequestMapping("/showContact")
	public String showContact(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		return "config/contact.html";
	}

	/**
	 * ユーザーからのお問合せを管理者に送信するメソッド.
	 * 
	 * @param loginUser          ログインユーザー情報
	 * @param subject            問合せのカテゴリ
	 * @param content            問合せ内容
	 * @param redirectAttributes フラッシュスコープ
	 * @return お問合せ画面
	 */
	@RequestMapping("/contact")
	public String contact(@AuthenticationPrincipal LoginUser loginUser, String subject, String content,
			RedirectAttributes redirectAttributes) {
		receiveContactService.receiveContact(loginUser.getUser(), subject, content);
		redirectAttributes.addFlashAttribute("success", "お問合せ内容を送信しました。");
		return "redirect:/config/showContact";
	}

	/**
	 * 会員登録解除画面に遷移するメソッド.
	 * 
	 * @param loginUser ログインユーザー情報
	 * @param model     リクエストスコープ
	 * @return 会員登録解除画面
	 */
	@RequestMapping("/showWithdraw")
	public String showWithdraw(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		return "config/withdraw.html";
	}
	
	@RequestMapping("/withdraw")
	public String withdraw(@AuthenticationPrincipal LoginUser loginUser, Model model) {
		withDrawService.withdraw(loginUser.getUser().getId());
		LOGGER.info("ユーザーID:"+loginUser.getUser().getId()+"の全てのユーザー情報を削除しました");
		try {
			httpServletRequest.logout();
		} catch (ServletException e) {
			e.printStackTrace();
		}
		return "redirect:/thanks";
	}

}
