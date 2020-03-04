package com.noodle.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.noodle.domain.PreUser;

/**
 * 仮ユーザー情報を格納するpre_usersテーブルを操作するリポジトリークラス.
 * @author yuichi
 *
 */
@Repository
public class PreUserRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;
	
	/** ロギング処理 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);	
	
	/**
	 * 仮ユーザー情報を挿入するメソッド.
	 * @param preUser 仮ユーザー情報
	 */
	public void insert(PreUser preUser) {
		String sql = "INSERT INTO pre_users(name,email,password,zipcode,address,telephone, uuid) VALUES(:name,:email,:password,:zipcode,:address,:telephone,:uuId)";
		SqlParameterSource param = new BeanPropertySqlParameterSource(preUser);
		template.update(sql, param);
		LOGGER.info("仮ユーザー情報をpre_usersテーブルに追加しました");
	}
	
	/**
	 * UUIDで検索した仮ユーザー情報を削除するメソッド.
	 * @param uuId UUID
	 */
	public void deleteByUuId(String uuId) {
		String sql = "DELETE FROM pre_users WHERE uuid = :uuid";
		SqlParameterSource param = new MapSqlParameterSource().addValue("uuid", uuId);
		template.update(sql, param);
	}
	
}
