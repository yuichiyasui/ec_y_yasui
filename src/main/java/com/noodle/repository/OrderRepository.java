package com.noodle.repository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.noodle.domain.Item;
import com.noodle.domain.Order;
import com.noodle.domain.OrderItem;
import com.noodle.domain.OrderTopping;
import com.noodle.domain.Topping;
import com.noodle.service.AddToCartService;

/**
 * ordersテーブルを操作するレポジトリークラス.
 * @author yuichi
 *
 */
@Repository
public class OrderRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;
	
	/** ロギング処理 */
	private static final Logger LOGGER
	= LoggerFactory.getLogger(AddToCartService.class);
	
	/**	Ordersテーブルのみを参照する場合 */
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
	
	/**	結合した複数のテーブルを参照する場合 */
	private final ResultSetExtractor<List<Order>> ORDER_EXTRACTOR = (rs)->{
		OrderItem orderItem = null;
		Order order = null;
		List<OrderTopping> orderToppingList = null;
		List<Order> orderList = new ArrayList<>();
		List<OrderItem> orderItemList = null;
		int preOrderId = 0;
		int preOrderItemId = 0;
		// ログで商品情報の取得の実行回数を表示する用
		int orderItemCount = 0;
		// ログでトッピング情報の取得の実行回数を表示する用
		int orderToppingCount = 0;
		while(rs.next()) {
			// Orderオブジェクトの作成(前回とidが重複しなければ実行)
			if(preOrderId != rs.getInt("o_id")) {
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
				LOGGER.info("注文情報を取得しました");
			}
			// OrderItemオブジェクトの作成(前回とidが重複しなければ実行)
			if(preOrderItemId != rs.getInt("oi_id")) {
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
				// カウンター
				orderItemCount++;
				LOGGER.info(orderItemCount + "件目の注文商品情報を取得しました");
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
			// カウンター
			orderToppingCount++;
			LOGGER.info(orderToppingCount + "件目の注文トッピング情報を取得しました");
		}
		return orderList;
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
	 * 利用されるクラス:AddToCartService
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
		Order order = template.queryForObject(sql, param, ORDER_ROW_MAPPER);
		return order.getId();
	}
	
	/**
	 * 主キーで注文情報を取得するメソッド.
	 * 利用されるクラス:
	 * @param id 注文ID
	 * @return 注文情報
	 */
	public Order load(Integer id) {
		String sql = "SELECT " + 
				"o.id AS o_id," + 
				"o.user_id AS o_user_id," + 
				"o.status AS o_status," + 
				"o.total_price AS o_total_price," + 
				"o.order_date AS o_order_date," + 
				"o.destination_name AS o_destination_name," + 
				"o.destination_email AS o_destination_email," + 
				"o.destination_zipcode AS o_destination_zipcode," + 
				"o.destination_address AS o_destination_address," + 
				"o.destination_tel AS o_destination_tel," + 
				"o.delivery_time AS o_delivery_time," + 
				"o.payment_method AS o_payment_method," + 
				"oi.id AS oi_id," + 
				"oi.item_id AS oi_item_id," + 
				"oi.order_id AS oi_order_id," + 
				"oi.quantity AS oi_quantity," + 
				"oi.size AS oi_size," + 
				"item.id AS it_id," + 
				"item.name AS it_name," + 
				"item.description AS it_description," + 
				"item.price_m AS it_price_m," + 
				"item.price_l AS it_price_l," + 
				"item.image_path AS it_image_path," + 
				"ot.id AS ot_id," + 
				"ot.topping_id AS ot_topping_id," + 
				"ot.order_item_id AS ot_order_item_id," + 
				"top.id AS top_id," + 
				"top.name AS top_name," + 
				"top.price_m AS top_price_m," + 
				"top.price_l AS top_price_l " + 
				"FROM orders AS o " + 
				"LEFT OUTER JOIN order_items     AS oi ON o.id  = oi.order_id " + 
				"LEFT OUTER JOIN items           AS item ON item.id = oi.item_id " + 
				"LEFT OUTER JOIN order_toppings  AS ot ON oi.id = ot.order_item_id " + 
				"LEFT OUTER JOIN toppings        AS top ON top.id = ot.topping_id " + 
				"WHERE o.id=:id " + 
				"ORDER BY oi.id ASC, ot.id ASC";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("id", id);
		return template.query(sql, param, ORDER_EXTRACTOR).get(0);
	}
	
	/**
	 * ユーザーIDとステータス0で注文情報を取得するメソッド.
	 * 利用されるクラス:ShowCartListService
	 * @param userId ユーザーID
	 * @return 注文情報
	 */
	public Order findOrderByUserIdAndStatus(Integer userId) {
		String sql = "SELECT " + 
				"o.id AS o_id," + 
				"o.user_id AS o_user_id," + 
				"o.status AS o_status," + 
				"o.total_price AS o_total_price," + 
				"o.order_date AS o_order_date," + 
				"o.destination_name AS o_destination_name," + 
				"o.destination_email AS o_destination_email," + 
				"o.destination_zipcode AS o_destination_zipcode," + 
				"o.destination_address AS o_destination_address," + 
				"o.destination_tel AS o_destination_tel," + 
				"o.delivery_time AS o_delivery_time," + 
				"o.payment_method AS o_payment_method," + 
				"oi.id AS oi_id," + 
				"oi.item_id AS oi_item_id," + 
				"oi.order_id AS oi_order_id," + 
				"oi.quantity AS oi_quantity," + 
				"oi.size AS oi_size," + 
				"item.id AS it_id," + 
				"item.name AS it_name," + 
				"item.description AS it_description," + 
				"item.price_m AS it_price_m," + 
				"item.price_l AS it_price_l," + 
				"item.image_path AS it_image_path," + 
				"ot.id AS ot_id," + 
				"ot.topping_id AS ot_topping_id," + 
				"ot.order_item_id AS ot_order_item_id," + 
				"top.id AS top_id," + 
				"top.name AS top_name," + 
				"top.price_m AS top_price_m," + 
				"top.price_l AS top_price_l " + 
				"FROM orders AS o " + 
				"LEFT OUTER JOIN order_items     AS oi ON o.id  = oi.order_id " + 
				"LEFT OUTER JOIN items           AS item ON item.id = oi.item_id " + 
				"LEFT OUTER JOIN order_toppings  AS ot ON oi.id = ot.order_item_id " + 
				"LEFT OUTER JOIN toppings        AS top ON top.id = ot.topping_id " + 
				"WHERE o.user_id=:user_id AND o.status=0 " + 
				"ORDER BY o.id DESC, oi.id ASC, ot.id ASC";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("user_id", userId);
		Order order = template.query(sql, param, ORDER_EXTRACTOR).get(0);
		return order;
	}
	
	/**
	 * 注文情報のユーザーIDを仮IDから本IDに更新するメソッド.
	 * 利用されるクラス:LoginService
	 * @param userId 本ID
	 * @param preId 仮ID
	 */
	public void updateOrdersUserId(Integer userId, Integer preId) {
		String sql = "UPDATE orders SET user_id = :userId WHERE user_id = :preId";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("userId", userId)
				.addValue("preId", preId);
		template.update(sql, param);
	}
	
	/**
	 * 受け取った注文情報を基にordersテーブルの注文情報を更新するメソッド.
	 * 利用されるクラス:ReceiveOrderService
	 * @param order 注文情報
	 */
	public void update(Order order) {
		String sql = "UPDATE orders SET status=:status, total_price=:total_price,"
				+ "order_date=:order_date, destination_name=:destination_name,"
				+ "destination_email=:destination_email,"
				+ "destination_zipcode=:destination_zipcode,"
				+ "destination_address=:destination_address,"
				+ "destination_tel=:destination_tel,"
				+ "delivery_time=:delivery_time,payment_method=:payment_method WHERE id=:id";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("id", order.getId())
				.addValue("status", order.getStatus())
				.addValue("total_price", order.getCalcTotalPrice()+order.getTax())
				.addValue("order_date", order.getOrderDate())
				.addValue("destination_name", order.getDestinationName())
				.addValue("destination_email", order.getDestinationEmail())
				.addValue("destination_zipcode", order.getDestinationZipcode())
				.addValue("destination_address", order.getDestinationAddress())
				.addValue("destination_tel", order.getDestinationTel())
				.addValue("delivery_time", order.getDeliveryTime())
				.addValue("payment_method", order.getPaymentMethod());
		template.update(sql, param);
	}
		
	/**
	 * ユーザーIDで注文情報を削除するメソッド.
	 * 利用されるクラス:LoginService
	 * @param userId ユーザーID
	 */
	public void deleteByUserId(Integer userId) {
		String sql = "DELETE FROM orders WHERE user_id = :userId";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		template.update(sql, param);
	}
}
