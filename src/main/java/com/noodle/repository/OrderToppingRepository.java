package com.noodle.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.noodle.domain.OrderTopping;

/**
 * order_toppingsテーブルを操作するレポジトリークラス.
 * @author yuichi
 *
 */
@Repository
public class OrderToppingRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;
	
	/**
	 * 注文トッピングをInsertするメソッド.
	 * @param orderTopping 注文トッピング情報
	 */
	public void insert(OrderTopping orderTopping) {
		String sql = "INSERT INTO order_toppings(topping_id,order_item_id) "
				+ "VALUES(:topping_id,:order_item_id)";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("topping_id", orderTopping.getToppingId())
				.addValue("order_item_id", orderTopping.getOrderItemId());
		template.update(sql, param);
	}
}
