package com.noodle.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.domain.LoginUser;
import com.noodle.domain.User;
import com.noodle.form.ChangeInfoForm;
import com.noodle.repository.UserRepository;

/**
 * ユーザー情報の変更を行うサービスクラス.
 * 
 * @author yuichi
 *
 */
@Service
@Transactional
public class ChangeInfoService {

	@Autowired
	private UserRepository userRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(ChangeInfoService.class);

	/**
	 * フォームから受け取った情報を基にユーザー情報を更新するメソッド.
	 * 
	 * @param form 受け取った更新するユーザー情報
	 */
	public void changeInfo(ChangeInfoForm form, @AuthenticationPrincipal LoginUser loginUser) {
		User user = new User();
		user.setId(form.getId());
		user.setName(form.getName());
		user.setEmail(form.getEmail());
		user.setZipcode(form.getZipcode());
		user.setAddress(form.getAddress());
		user.setTelephone(form.getTelephone());
		userRepository.update(user);
		loginUser.setUser(user);
		LOGGER.info("ユーザー情報を更新しました");
	}
}
