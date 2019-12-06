package com.noodle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.domain.Item;
import com.noodle.repository.ItemRepository;
import com.noodle.repository.ToppingRepository;

/**
 * 商品詳細を取得するサービスクラス.
 * @author yuichi
 *
 */
@Service
@Transactional
public class ShowItemDetailService {

	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private ToppingRepository toppingRepository;
	
	
	/**
	 * 商品IDを受け取って商品情報を取得するメソッド.
	 * @param id 商品ID
	 * @return 商品情報
	 */
	public Item showItemDetail(Integer id) {
		Item item = itemRepository.load(id);
		item.setToppingList(toppingRepository.findAll());
		return item;
	}
	
}
