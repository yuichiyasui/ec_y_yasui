package com.noodle.repository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.noodle.domain.Item;
import com.noodle.domain.Order;
import com.noodle.domain.OrderItem;
import com.noodle.domain.OrderTopping;
import com.noodle.domain.Topping;

/**
 * ordersテーブルを操作するレポジトリークラス.
 * 
 * @author yuichi
 *
 */
@Repository
public class OrderRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;

	/** ロギング処理 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderRepository.class);

	/** Ordersテーブルのみを参照する場合 */
	private final static RowMapper<Order> ORDER_ROW_MAPPER = (rs, i) -> {
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

	/** 結合した複数のテーブルを参照する場合 */
	private final ResultSetExtractor<List<Order>> ORDER_EXTRACTOR = (rs) -> {
		OrderItem orderItem = null;
		Order order = null;
		List<OrderTopping> orderToppingList = null;
		List<Order> orderList = new ArrayList<>();
		List<OrderItem> orderItemList = null;
		int preOrderId = 0;
		int preOrderItemId = 0;
		while (rs.next()) {
			// Orderオブジェクトの作成(前回とidが重複しなければ実行)
			if (preOrderId != rs.getInt("o_id")) {
				order = new Order();
				order.setId(rs.getInt("o_id"));
				order.setUserId(rs.getInt("o_user_id"));
				order.setStatus(rs.getInt("o_status"));
				order.setTotalPrice(rs.getInt("o_total_price"));
				order.setOrderDate(rs.getDate("o_order_date"));
				order.setDestinationName(rs.getString("o_destination_name"));
				order.setDestinationEmail(rs.getString("o_destination_email"));
				order.setDestinationZipcode(rs.getString("o_destination_zipcode"));
				order.setDestinationAddress(rs.getString("o_destination_address"));
				order.setDestinationTel(rs.getString("o_destination_tel"));
				order.setDeliveryTime(rs.getTimestamp("o_delivery_time"));
				order.setPaymentMethod(rs.getInt("o_payment_method"));
				orderItemList = new ArrayList<>();
				order.setOrderItemList(orderItemList);
				preOrderId = rs.getInt("o_id");
				orderList.add(order);
			}
			// OrderItemオブジェクトの作成(前回とidが重複しなければ実行)
			if (preOrderItemId != rs.getInt("oi_id")) {
				orderItem = new OrderItem();
				orderItem.setId(rs.getInt("oi_id"));
				orderItem.setItemId(rs.getInt("oi_item_id"));
				orderItem.setOrderId(rs.getInt("oi_order_id"));
				orderItem.setQuantity(rs.getInt("oi_quantity"));
				// String型からchar型への変換
				char[] value = rs.getString("oi_size").toCharArray();
				orderItem.setSize(value[0]);
				// 注文商品に商品情報をセット
				Item item = new Item();
				item.setId(rs.getInt("it_id"));
				item.setName(rs.getString("it_name"));
				item.setDescription(rs.getString("it_description"));
				item.setPriceM(rs.getInt("it_price_m"));
				item.setPriceL(rs.getInt("it_price_l"));
				item.setImagePath(rs.getString("it_image_path"));
				orderItem.setItem(item);
				orderToppingList = new ArrayList<>();
				orderItem.setOrderToppingList(orderToppingList);
				orderItemList.add(orderItem);
				preOrderItemId = rs.getInt("oi_id");
			}
			// OrderToppingオブジェクトの作成
			OrderTopping orderTopping = new OrderTopping();
			orderTopping.setId(rs.getInt("ot_id"));
			orderTopping.setToppingId(rs.getInt("ot_topping_id"));
			orderTopping.setOrderItemId(rs.getInt("ot_order_item_id"));
			// 注文トッピングにトッピング情報
			Topping topping = new Topping();
			topping.setId(rs.getInt("top_id"));
			topping.setName(rs.getString("top_name"));
			topping.setPriceM(rs.getInt("top_price_m"));
			topping.setPriceL(rs.getInt("top_price_l"));
			orderTopping.setTopping(topping);
			orderToppingList.add(orderTopping);
		}
		return orderList;
	};

	/**
	 * オーダー情報をInsertするメソッド.
	 * 
	 * @param order オーダー情報
	 */
	public void insert(Order order) {
		String sql = "INSERT INTO orders(user_id,status,total_price) VALUES(:userId,:status,:totalPrice);";
		SqlParameterSource param = new BeanPropertySqlParameterSource(order);
		template.update(sql, param);
	}


	/**
	 * 注文情報を挿入してその挿入した注文情報を返すメソッド.
	 * @param order 挿入する注文情報
	 * @return 挿入した注文情報
	 */
	public Order insertWithReturnOrder(Order order) {
		String sql = "INSERT INTO orders(user_id,status,total_price) VALUES(:userId,:status,:totalPrice) RETURNING id,user_id,status,total_price,order_date,"
				+ "destination_name,destination_email,destination_zipcode,"
				+ "destination_address,destination_tel,delivery_time,payment_method";
		SqlParameterSource param = new BeanPropertySqlParameterSource(order);
		return template.queryForObject(sql, param, ORDER_ROW_MAPPER);
	}

	/**
	 * ユーザーIDとステータスでオーダー情報を取得するメソッド.
	 * 
	 * @param userId ユーザーID
	 * @return オーダー情報
	 */
	public Order findByUserIdAndStatus(Integer userId, int status) {
		String sql = "SELECT id,user_id,status,total_price,order_date,"
				+ "destination_name,destination_email,destination_zipcode,"
				+ "destination_address,destination_tel,delivery_time,payment_method "
				+ "FROM orders WHERE user_id=:user_id AND status=:status";
		SqlParameterSource param = new MapSqlParameterSource().addValue("user_id", userId).addValue("status", status);
		Order order;
		try {
			order = template.queryForObject(sql, param, ORDER_ROW_MAPPER);
			return order;
		} catch (DataAccessException e) {
			LOGGER.info("ユーザーID:" + userId + "の注文がありませんでした");
			return null;
		}
	}

	/**
	 * 主キーで注文情報を取得するメソッド. 利用されるクラス:ReceiveOrderService
	 * 
	 * @param id 注文ID
	 * @return 注文情報
	 */
	public Order load(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT o.id AS o_id, o.user_id AS o_user_id, o.status AS o_status,");
		sql.append("o.total_price AS o_total_price, o.order_date AS o_order_date,");
		sql.append("o.destination_name AS o_destination_name, o.destination_email AS o_destination_email,");
		sql.append("o.destination_zipcode AS o_destination_zipcode, o.destination_address AS o_destination_address,");
		sql.append("o.destination_tel AS o_destination_tel, o.delivery_time AS o_delivery_time,");
		sql.append("o.payment_method AS o_payment_method, oi.id AS oi_id, oi.item_id AS oi_item_id,");
		sql.append("oi.order_id AS oi_order_id, oi.quantity AS oi_quantity, oi.size AS oi_size,");
		sql.append("item.id AS it_id, item.name AS it_name, item.description AS it_description,");
		sql.append("item.price_m AS it_price_m, item.price_l AS it_price_l, item.image_path AS it_image_path,");
		sql.append("ot.id AS ot_id, ot.topping_id AS ot_topping_id, ot.order_item_id AS ot_order_item_id,");
		sql.append("top.id AS top_id, top.name AS top_name, top.price_m AS top_price_m,");
		sql.append("top.price_l AS top_price_l FROM orders AS o ");
		sql.append("LEFT OUTER JOIN order_items     AS oi ON o.id  = oi.order_id ");
		sql.append("LEFT OUTER JOIN items           AS item ON item.id = oi.item_id ");
		sql.append("LEFT OUTER JOIN order_toppings  AS ot ON oi.id = ot.order_item_id ");
		sql.append("LEFT OUTER JOIN toppings        AS top ON top.id = ot.topping_id WHERE o.id=:id ");
		sql.append("ORDER BY oi.id ASC, ot.id ASC");
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		return template.query(sql.toString(), param, ORDER_EXTRACTOR).get(0);
	}

	/**
	 * ユーザーIDとステータスで注文情報を取得するメソッド. 利用されるクラス:ShowCartListService,
	 * ShowOrderHistoryService
	 * 
	 * @param userId ユーザーID
	 * @param status 注文ステータス
	 * @return 注文情報のリスト
	 */
	public List<Order> findOrderByUserIdAndStatus(Integer userId, Integer status) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT o.id AS o_id,o.user_id AS o_user_id,o.status AS o_status,");
		sql.append("o.total_price AS o_total_price, o.order_date AS o_order_date,");
		sql.append("o.destination_name AS o_destination_name, o.destination_email AS o_destination_email,");
		sql.append("o.destination_zipcode AS o_destination_zipcode, o.destination_address AS o_destination_address,");
		sql.append("o.destination_tel AS o_destination_tel, o.delivery_time AS o_delivery_time,");
		sql.append("o.payment_method AS o_payment_method, oi.id AS oi_id, oi.item_id AS oi_item_id,");
		sql.append("oi.order_id AS oi_order_id, oi.quantity AS oi_quantity, oi.size AS oi_size,");
		sql.append("item.id AS it_id, item.name AS it_name, item.description AS it_description,");
		sql.append("item.price_m AS it_price_m, item.price_l AS it_price_l, item.image_path AS it_image_path,");
		sql.append("ot.id AS ot_id, ot.topping_id AS ot_topping_id, ot.order_item_id AS ot_order_item_id,");
		sql.append("top.id AS top_id, top.name AS top_name, top.price_m AS top_price_m,");
		sql.append("top.price_l AS top_price_l FROM orders AS o ");
		sql.append("LEFT OUTER JOIN order_items     AS oi ON o.id  = oi.order_id ");
		sql.append("LEFT OUTER JOIN items           AS item ON item.id = oi.item_id ");
		sql.append("LEFT OUTER JOIN order_toppings  AS ot ON oi.id = ot.order_item_id ");
		sql.append("LEFT OUTER JOIN toppings        AS top ON top.id = ot.topping_id ");
		sql.append("WHERE o.user_id=:userId AND o.status=:status ORDER BY o.id DESC, oi.id ASC, ot.id ASC");
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId).addValue("status", status);
		List<Order> orderList;
		try {
			orderList = template.query(sql.toString(), param, ORDER_EXTRACTOR);
		} catch (NullPointerException e) {
			LOGGER.info("注文情報が存在しませんでした");
			orderList = null;
		}
		return orderList;
	}
	


	/**
	 * 注文情報のユーザーIDを仮IDから本IDに更新するメソッド. 利用されるクラス:LoginService
	 * 
	 * @param userId 本ID
	 * @param preId  仮ID
	 */
	public void updateOrdersUserId(Integer userId, Integer preId) {
		String sql = "UPDATE orders SET user_id = :userId WHERE user_id = :preId";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId).addValue("preId", preId);
		template.update(sql, param);
	}

	/**
	 * 受け取った注文情報を基にordersテーブルの注文情報を更新するメソッド. 
	 * 利用されるクラス:ReceiveOrderService
	 * 
	 * @param order 注文情報
	 */
	public void update(Order order) {
		String sql = "UPDATE orders SET status=:status, total_price=:totalPrice,"
				+ "order_date=:orderDate, destination_name=:destinationName," + "destination_email=:destinationEmail,"
				+ "destination_zipcode=:destinationZipcode," + "destination_address=:destinationAddress,"
				+ "destination_tel=:destinationTel,"
				+ "delivery_time=:deliveryTime,payment_method=:paymentMethod WHERE id=:id";
		SqlParameterSource param = new BeanPropertySqlParameterSource(order);
		template.update(sql, param);
	}

	/**
	 * 注文IDで注文情報を削除するメソッド. 
	 * 利用されるクラス:LoginService
	 * @param id 注文ID
	 */
	public void deleteById(Integer id) {
		String sql = "DELETE FROM orders WHERE id=:id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		template.update(sql, param);
		LOGGER.info("注文ID:" + id +"の注文情報を削除しました");
	}
}
