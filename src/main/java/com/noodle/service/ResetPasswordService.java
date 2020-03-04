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
import com.noodle.domain.User;
import com.noodle.repository.UserRepository;

/**
 * パスワードの再設定を行うサービスクラス.
 * @author yuichi
 *
 */
@Service
@Transactional
public class ResetPasswordService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EnvironmentsConfiguration envConfig;
	@Autowired
	private MailSender sender;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordService.class);
	
	/**
	 * ユーザー情報にUUIDを付与してパスワード再設定用のURLを送信するメソッド.
	 * @param user ユーザー情報
	 */
	public void sendResetMail(User user) {
		String uuId = UUID.randomUUID().toString();
		userRepository.updateUuId(user.getId(),uuId);
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(envConfig.getMail());
		msg.setTo(user.getEmail());
		msg.setSubject("[らくらく軒]パスワードの再設定用のURL");
		StringBuilder str = new StringBuilder();
		str.append(user.getName());
		str.append("様\n\nいつもらくらく軒をご利用いただき誠にありがとうございます。\n");
		str.append("本メール記載の下記リンクから、パスワードの再設定が可能でございます。\n\n");
		str.append(envConfig.getUrl() + "/config/showResetPassword?uuId=" + uuId);
		str.append("\n\nお手数ですがご確認よろしくお願いいたします。\n");
		str.append("(パスワードの変更を中止する場合は、特段何か操作をする必要はございません)\n\n\n");
		str.append("卍----------------------------------------------------------卍\n\n　NOODLE DELIVERY SHOP - らくらく軒 -\n　"
				+ envConfig.getUrl() + "\n\n卍----------------------------------------------------------卍");
		msg.setText(str.toString());
		sender.send(msg);
		LOGGER.info(user.getEmail()+"にパスワード再設定用のメールを送信しました");
	}
	
	/**
	 * UUIDでユーザーIDが存在するか確認し取得するメソッド.
	 * @param uuId UUID
	 * @return ユーザーID
	 */
	public Integer getUserByUuId(String uuId) {
		return userRepository.findByUuId(uuId);
	}
	
	/**
	 * パスワードを更新し、UUIDを空にするメソッド.
	 * @param id ユーザーID
	 * @param password パスワード
	 */
	public void updatePasswordAndUuId(Integer id, String password) {
		userRepository.updatePasswordAndUuId(id, passwordEncoder.encode(password));
	}
	
}
