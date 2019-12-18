package com.noodle.repository;

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
 * @author yuichi
 *
 */
@Repository
public class UserRepository {
	@Autowired
	private NamedParameterJdbcTemplate template;
	
	private final static RowMapper<User> USER_ROW_MAPPER = (rs,i)->{
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
	 * @param userId ユーザーID
	 * @return ユーザー情報
	 */
	public User load(Integer userId) {
		String sql = "SELECT id,name,email,password,zipcode,address,telephone "
				+ "FROM users WHERE id = :user_id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("user_id", userId);
		return template.queryForObject(sql, param, USER_ROW_MAPPER);
	}
	
	/**
	 * メールアドレスでユーザー情報を取得するメソッド.
	 * @param email メールアドレス
	 * @return ユーザー情報 / 存在しなければnullを返す
	 */
	public User findByEmail(String email) {
		String sql = "SELECT id,name,email,password,zipcode,address,telephone "
				+ "FROM users WHERE email=:email";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("email", email);	
		try {
			return template.queryForObject(sql, param, USER_ROW_MAPPER);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}			
	}
	
	/**
	 * ユーザー情報をDBにINSERTするメソッド.
	 * @param user ユーザー情報
	 */
	public void insert(User user) {
		String sql = "INSERT INTO users(name,email,password,zipcode,address,telephone) "
				+ "VALUES(:name,:email,:password,:zipcode,:address,:telephone)";
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		template.update(sql, param);
	}
}
