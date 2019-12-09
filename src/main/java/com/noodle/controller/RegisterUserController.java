package com.noodle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noodle.form.RegisterUserForm;
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
	
	/**
	 * ユーザー登録処理を行うメソッド.
	 * @param form 
	 * @return
	 */
	@RequestMapping("/registerUser")
	public String registerUser(
			@Validated RegisterUserForm form,
			BindingResult result,
			Model model
			) {
		if(result.hasErrors()) {
			return "forward:/showLogin";
		}
		registerUserService.RegisterUser(form);
		return "forward:/showLogin";
	}
	
}
