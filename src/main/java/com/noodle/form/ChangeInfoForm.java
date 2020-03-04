package com.noodle.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ChangeInfoForm {
	/**	ID */
	private Integer id;
	/** ユーザー名 */
	@NotBlank(message = "*名前の入力は必須です")
	private String name;
	/** メールアドレス */
	@NotBlank(message = "*メールアドレスを入力して下さい")
	@Email(message = "*メールアドレスの形式が不正です")
	private String email;
	/** 郵便番号 */
	@NotBlank(message = "*郵便番号の入力は必須です")
	@Pattern(regexp = "^[0-9]{7}$", message = "*郵便番号の形式が不正です")
	private String zipcode;
	/** 住所 */
	@NotBlank(message = "*住所の入力は必須です")
	private String address;
	/** 電話番号 */
	@Pattern(regexp = "^[0-9]*$", message = "*半角数字で入力してください")
	@NotBlank(message = "*電話番号を入力して下さい")
	private String telephone;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}
