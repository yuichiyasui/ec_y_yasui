package com.noodle.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.noodle.domain.Topping;

/**
 * toppingsテーブルを操作するレポジトリークラス.
 * @author yuichi
 *
 */
@Repository
public class ToppingRepository {
	@Autowired
	private NamedParameterJdbcTemplate template;
	
	private final static RowMapper<Topping> TOPPING_ROW_MAPPER = (rs,i)->{
		Topping topping = new Topping();
		topping.setId(rs.getInt("id"));
		topping.setName(rs.getString("name"));
		topping.setPriceM(rs.getInt("price_m"));
		topping.setPriceL(rs.getInt("price_l"));
		return topping;
	};
	
	/**
	 * 全件検索を行うメソッド.
	 * (IDで昇順)
	 * @return トッピングリスト
	 */
	public List<Topping> findAll(){
		String sql = "SELECT id, name, price_m, price_l "
				+ "FROM toppings ORDER BY id ASC";
		return template.query(sql, TOPPING_ROW_MAPPER);
	}	
	
	/**
	 * 主キー検索を行うメソッド.
	 * @param id トッピングID
	 * @return トッピング情報
	 */
	public Topping load(Integer id) {
		String sql = "SELECT id, name, price_m, price_l "
				+ "FROM toppings WHERE id=:id";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("id", id);
		return template.queryForObject(sql, param, TOPPING_ROW_MAPPER);
	}
}
