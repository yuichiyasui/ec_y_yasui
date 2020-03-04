package com.noodle.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 環境変数を参照するクラス.
 * @author yuichi
 *
 */
@Component
@ConfigurationProperties(prefix = "environments")
public class EnvironmentsConfiguration {

	/**	ドメインURL */
	private String url;
	/**	メール送信時の送信用アカウントのメールアドレス */
	private String mail;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
}
