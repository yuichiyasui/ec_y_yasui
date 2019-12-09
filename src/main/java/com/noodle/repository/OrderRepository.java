package com.noodle.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.noodle.domain.Order;

/**
 * ordersテーブルを操作するレポジトリークラス.
 * @author yuichi
 *
 */
@Repository
public class OrderRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;
	
	private final static RowMapper<Order> ORDER_ROW_MAPPER = (rs,i)->{
		Order order = new Order();
		order.setId(rs.getInt("id"));
		order.setUserId(rs.getInt("user_id"));
		order.setStatus(rs.getInt("status"));
		order.setTotalPrice(rs.getInt("total_price"));
		order.setOrderDate(rs.getDate("order_date"));
		order.setDestinationName(rs.getString("destination_name"));
		order.setDestinationEmail(rs.getString("destination_email"));
		order.setDestinationZipcode(rs.getString("destination_zipcode"));
		order.setDestinationAddress(rs.getString("destination_address"));
		order.setDestinationTel(rs.getString("destination_tel"));
		order.setDeliveryTime(rs.getTimestamp("delivery_time"));
		order.setPaymentMethod(rs.getInt("payment_method"));
		return order;
	};
	
	/**
	 * オーダー情報をInsertするメソッド.
	 * @param order オーダー情報
	 */
	public void insert(Order order) {
		String sql = "INSERT INTO orders(user_id,status,total_price) "
				+ "VALUES(:user_id,:status,:total_price);";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("user_id", order.getUserId())
				.addValue("status", order.getStatus())
				.addValue("total_price", order.getTotalPrice());
		template.update(sql, param);
	}
	
	/**
	 * ユーザーIDとステータス0(注文前)でオーダー情報を取得するメソッド.
	 * @param userId ユーザーID
	 * @return オーダー情報
	 */
	public Order findByUserIdAndStatus(Integer userId) {
		String sql = "SELECT id,user_id,status,total_price,order_date,"
				+ "destination_name,destination_email,destination_zipcode,"
				+ "destination_address,destination_tel,delivery_time,payment_method "
				+ "FROM orders WHERE user_id=:user_id AND status=0";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("user_id", userId);
		Order order;
		try {
			order = template.queryForObject(sql, param, ORDER_ROW_MAPPER);
			return order;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * ユーザーIDとステータス0(注文前)でオーダーIDを取得するメソッド.
	 * @param userId ユーザーID
	 * @return オーダーID
	 */
	public int findIdByUserIdAndStatus(Integer userId) {
		String sql = "SELECT id,user_id,status,total_price,order_date,"
				+ "destination_name,destination_email,destination_zipcode,"
				+ "destination_address,destination_tel,delivery_time,payment_method "
				+ "FROM orders WHERE user_id=:user_id AND status=0";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("user_id", userId);
		List<Order> orderList = template.query(sql, param, ORDER_ROW_MAPPER);
		return orderList.get(0).getId();
	}
}
