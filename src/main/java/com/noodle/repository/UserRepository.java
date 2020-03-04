package com.noodle.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.noodle.domain.User;

/**
 * usersテーブルを操作するレポジトリークラス.
 * 
 * @author yuichi
 *
 */
@Repository
public class UserRepository {
	@Autowired
	private NamedParameterJdbcTemplate template;

	/** ロギング処理 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);

	private final static RowMapper<User> USER_ROW_MAPPER = (rs, i) -> {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setZipcode(rs.getString("zipcode"));
		user.setAddress(rs.getString("address"));
		user.setTelephone(rs.getString("telephone"));
		return user;
	};

	/**
	 * 主キー検索のメソッド.
	 * 
	 * @param userId ユーザーID
	 * @return ユーザー情報
	 */
	public User load(Integer userId) {
		String sql = "SELECT id,name,email,password,zipcode,address,telephone FROM users WHERE id = :user_id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("user_id", userId);
		return template.queryForObject(sql, param, USER_ROW_MAPPER);
	}

	/**
	 * メールアドレスでユーザー情報を取得するメソッド.
	 * 
	 * @param email メールアドレス
	 * @return ユーザー情報 / 存在しなければnullを返す
	 */
	public User findByEmail(String email) {
		String sql = "SELECT id,name,email,password,zipcode,address,telephone FROM users WHERE email=:email";
		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);
		try {
			return template.queryForObject(sql, param, USER_ROW_MAPPER);
		} catch (DataAccessException e) {
			LOGGER.info(email + " は登録されていないメールアドレスでした");
			return null;
		}
	}

	/**
	 * 仮ユーザー情報を本ユーザー情報に切り替えるメソッド.
	 * 
	 * @param uuId 仮ユーザーに付与されたUUID
	 * @return 挿入したユーザー情報
	 */
	public User switchFromPreUserToUser(String uuId) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO users(name,email,password,zipcode,address,telephone) ");
		sql.append("SELECT name,email,password,zipcode,address,telephone ");
		sql.append(
				"FROM pre_users WHERE pre_users.uuid = :uuId RETURNING id,name,email,password,zipcode,address,telephone");
		SqlParameterSource param = new MapSqlParameterSource().addValue("uuId", uuId);
		User user = template.queryForObject(sql.toString(), param, USER_ROW_MAPPER);
		return user;
	}

	/**
	 * ユーザー情報を変更するメソッド. 利用されるクラス：ChangeInfoService
	 * 
	 * @param user 変更するユーザー情報
	 */
	public void update(User user) {
		String sql = "UPDATE users SET name = :name, email=:email, zipcode = :zipcode, address=:address, telephone=:telephone WHERE id=:id";
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		template.update(sql, param);
		LOGGER.info("ユーザーID:" + user.getId() + "のユーザー情報を変更しました");
	}

	/**
	 * UUIDを変更するメソッド. 利用されるクラス：ResetPasswordService
	 * 
	 * @param id   ユーザーID
	 * @param uuId UUID
	 */
	public void updateUuId(Integer id, String uuId) {
		String sql = "UPDATE users SET uuid =:uuId WHERE id=:id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id).addValue("uuId", uuId);
		template.update(sql, param);
		LOGGER.info("ユーザーID:" + id + "にパスワード再設定用のUUIDを付与しました");
	}

	/** findByUuID用 */
	private static final RowMapper<Integer> ID_ROW_MAPPER = (rs, i) -> {
		return rs.getInt("id");
	};

	/**
	 * UUIDでユーザーIDを取得するメソッド. 利用されるクラス:ResetPasswordService
	 * 
	 * @param uuId UUID
	 * @return ユーザーID
	 */
	public Integer findByUuId(String uuId) {
		String sql = "SELECT id FROM users WHERE uuid=:uuId";
		SqlParameterSource param = new MapSqlParameterSource().addValue("uuId", uuId);
		Integer id = template.queryForObject(sql, param, ID_ROW_MAPPER);
		LOGGER.info("ユーザーID:" + id + "がパスワード再設定画面にアクセスしました");
		return id;
	}

	/**
	 * パスワードを更新しUUIDを空にするメソッド. 利用されるクラス:ResetPasswordService
	 * 
	 * @param id       ユーザーID
	 * @param password パスワード
	 */
	public void updatePasswordAndUuId(Integer id, String password) {
		String sql = "UPDATE users SET uuid='',password=:password WHERE id=:id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("password", password).addValue("id", id);
		template.update(sql, param);
		LOGGER.info("ユーザーID:" + id + "のパスワードを更新しました");
		LOGGER.info("ユーザーID:" + id + "のUUIDを削除しました");
	}

	/**
	 * ユーザーIDでユーザー情報、注文情報(商品、トッピング含む)を全て削除するメソッド.
	 * 利用されるクラス:WithdrawService
	 * @param id ユーザーID
	 */
	public void deleteAlldataByUserId(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append("WITH order_deleted AS (DELETE FROM orders WHERE user_id=:id RETURNING id)");
		sql.append(
				",order_item_deleted AS (DELETE FROM order_items WHERE order_id IN (SELECT id FROM order_deleted) RETURNING id )");
		sql.append(",user_deleted AS (DELETE FROM users WHERE id=:id) ");
		sql.append("DELETE FROM order_toppings WHERE order_item_id IN (SELECT id FROM order_item_deleted)");
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		template.update(sql.toString(), param);
	}

}
