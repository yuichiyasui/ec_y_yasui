package com.noodle.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * パスワードの再設定用のフォームクラス.
 * @author yuichi
 *
 */
public class ResetPasswordForm {
	/**	ユーザーID */
	private Integer id;
	/** パスワード(英大文字・小文字+数字+記号の4種を含む8桁以上) */
	@Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*[!\\x22\\#$%&@'()*+,\\-./_])[A-Za-z0-9!\\x22\\#$%&@'()*+,\\-./_]{8,}$", message = "*パスワードは英大文字・小文字、数字、記号の4種を含む8桁以上で設定してください")
	@NotBlank(message = "*パスワードの入力は必須です")
	private String password;
	/**	確認用パスワード */
	@NotBlank(message = "*確認用パスワードの入力は必須です")
	private String confirmPassword;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	@Override
	public String toString() {
		return "ResetPasswordForm [id=" + id + ", password=" + password + ", confirmPassword=" + confirmPassword + "]";
	}
}
