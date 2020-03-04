package com.noodle.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.common.EnvironmentsConfiguration;
import com.noodle.domain.PreUser;
import com.noodle.domain.User;
import com.noodle.form.RegisterUserForm;
import com.noodle.repository.PreUserRepository;
import com.noodle.repository.UserRepository;

/**
 * ユーザー登録処理を行うサービスクラス.
 * 
 * @author yuichi
 *
 */
@Service
@Transactional
public class RegisterUserService {

	@Autowired
	private PreUserRepository preUserRepository;
	@Autowired
	private UserRepository userRepository;
	/** パスワードの暗号化 */
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MailSender sender;
	@Autowired
	private EnvironmentsConfiguration envConfig;

	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterUserService.class);

	/**
	 * ユーザー登録処理を行うメソッド.
	 * 
	 * @param form register_user.htmlから受け取ったパラメータ
	 */
	public void RegisterPreUser(RegisterUserForm form) {
		PreUser preUser = new PreUser();
		preUser.setName(form.getName());
		preUser.setEmail(form.getEmail());
		preUser.setTelephone(form.getTelephone());
		preUser.setZipcode(form.getZipcode());
		preUser.setAddress(form.getAddress());
		// パスワードのハッシュ化
		preUser.setPassword(passwordEncoder.encode(form.getPassword()));
		// UUIDの生成→セット
		preUser.setUuId(UUID.randomUUID().toString());
		preUserRepository.insert(preUser);
		// 登録確認メールの送信
		mailAuth(preUser);
	}

	/**
	 * メールアドレスでユーザー情報を検索するメソッド.
	 * 
	 * @param email メールアドレス
	 * @return 検索されたユーザー情報
	 */
	public User findByEmail(String email) {
		User user = userRepository.findByEmail(email);
		return user;
	}

	/**
	 * ユーザー登録の本人確認メールの送信メソッド.
	 * 
	 * @param user 送信先のユーザー情報
	 * @param uuId UUID
	 */
	public void mailAuth(PreUser preUser) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(envConfig.getMail());
		msg.setTo(preUser.getEmail());
		msg.setSubject("[らくらく軒]ユーザー登録の確認");
		StringBuilder str = new StringBuilder();
		str.append(preUser.getName());
		str.append("様\n\nらくらく軒にご登録いただき誠にありがとうございます。\n");
		str.append("会員登録の完了にあたり本メールの下記リンクにアクセスいただき、\n");
		str.append("認証を行うことで本サービスがご利用いただけるようになります。\n\n");
		str.append(envConfig.getUrl() + "/registerUser/register?id=" + preUser.getUuId());
		str.append("\n\nお手数ですがご確認よろしくお願いいたします。\n");
		str.append("(上記リンクの有効期限は当日の24時までとなります。\n");
		str.append("有効期限を過ぎてしまった場合はお手数ですが再度ユーザー登録をお願いいたします。)\n\n\n");
		str.append("卍----------------------------------------------------------卍\n\n　NOODLE DELIVERY SHOP - らくらく軒 -\n　"
				+ envConfig.getUrl() + "\n\n卍----------------------------------------------------------卍");
		msg.setText(str.toString());
		sender.send(msg);
		LOGGER.info(preUser.getEmail() + "に登録確認メールを送信しました。");
	}

	public void registerUser(String uuId) {
		User user = userRepository.switchFromPreUserToUser(uuId);
		LOGGER.info("UUID:" + uuId + "の仮ユーザー情報を本ユーザー情報に切り替えました");
		preUserRepository.deleteByUuId(uuId);
		LOGGER.info("UUID:" + uuId + "の仮ユーザー情報を削除しました");
		/** 登録完了メール送信 */
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(envConfig.getMail());
		msg.setTo(user.getEmail());
		msg.setSubject("[らくらく軒]ユーザー登録の完了");
		StringBuilder str = new StringBuilder();
		str.append(user.getName());
		str.append(" 様\n\nらくらく軒にご登録いただき誠にありがとうございます。\n");
		str.append("ユーザー登録が完了いたしましたのでご連絡いたします。\n\n");
		str.append("今後ともらくらく軒をよろしくお願い申し上げます。\n\n\n");
		str.append("卍----------------------------------------------------------卍\n\n　NOODLE DELIVERY SHOP - らくらく軒 -\n　"
				+ envConfig.getUrl() + "\n\n卍----------------------------------------------------------卍");
		msg.setText(str.toString());
		sender.send(msg);
		LOGGER.info(user.getEmail() + "に登録完了メールを送信しました。");

	}
}
