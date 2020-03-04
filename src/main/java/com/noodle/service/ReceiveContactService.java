package com.noodle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.common.EnvironmentsConfiguration;
import com.noodle.domain.User;

/**
 * お問合せを受けて管理者宛にメールを送信するサービスクラス.
 * @author yuichi
 *
 */
@Service
@Transactional
public class ReceiveContactService {

	@Autowired
	private MailSender sender;
	@Autowired
	EnvironmentsConfiguration envConfig;

	/**
	 * お問合せを受けて管理者にメールを送信するサービスクラス.
	 * @param user ユーザー情報
	 * @param subject お問合せのジャンル
	 * @param content お問合せ内容
	 */
	public void receiveContact(User user, String subject, String content) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(envConfig.getMail());
		msg.setTo(envConfig.getMail());
		msg.setSubject("[らくらく軒]HPからお問合せがありました");
		StringBuilder str = new StringBuilder();
		str.append("管理者 さん\n\nHPからお問い合わせがありました。\n\n");
		str.append("【お問合せ内容】----------------------------------------------------------\n\n");
		str.append("▼名前\n");
		str.append(user.getName() + "さん ");
		str.append("\n\n▼ご連絡先\n");
		str.append(user.getEmail() + "　" + user.getTelephone());
		str.append("\n\n▼お問合せカテゴリ\n");
		str.append(subject);
		str.append("\n\n▼お問合せ内容\n");
		str.append(content);
		str.append("\n\n----------------------------------------------------------------------------------\n\n");
		msg.setText(str.toString());
		sender.send(msg);
	}

}
