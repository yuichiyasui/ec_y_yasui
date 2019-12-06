package com.noodle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.domain.Item;
import com.noodle.repository.ItemRepository;

/**
 * 商品一覧を表示するサービスクラス
 * @author yuichi
 *
 */
@Service
@Transactional
public class ShowItemListService {
	@Autowired
	private ItemRepository itemRepository;
	
	/**
	 * 商品一覧に表示する商品リストを取得するメソッド.
	 * @return 商品リスト
	 */
	public List<Item> showItemList(){
		return itemRepository.findAll();
	}
	
}
