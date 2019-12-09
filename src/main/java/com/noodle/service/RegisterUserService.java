package com.noodle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.domain.User;
import com.noodle.form.RegisterUserForm;
import com.noodle.repository.UserRepository;

/**
 * ユーザー登録処理を行うサービスクラス.
 * @author yuichi
 *
 */
@Service
@Transactional
public class RegisterUserService {

	@Autowired
	private UserRepository userRepository;
	
	/**
	 * ユーザー登録処理を行うメソッド.
	 * @param form register_user.htmlから受け取ったパラメータ
	 */
	public void RegisterUser(RegisterUserForm form) {
		User user = new User();
		user.setName(form.getName());
		user.setEmail(form.getEmail());
		user.setTelephone(form.getTelephone());
		user.setZipcode(form.getZipcode());
		user.setAddress(form.getAddress());
		user.setPassword(form.getPassword());
		userRepository.insert(user);
	}
}
