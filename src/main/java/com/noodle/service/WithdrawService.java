package com.noodle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noodle.repository.UserRepository;

/**
 * 退会処理を行うサービスクラス.
 * @author yuichi
 *
 */
@Service
@Transactional
public class WithdrawService {

	@Autowired
	private UserRepository userRepository;
	
	/**
	 * 退会処理(全てのユーザー情報の削除)を行うメソッド.
	 * @param userId ユーザーID
	 */
	public void withdraw(Integer userId) {
		userRepository.deleteAlldataByUserId(userId);
	}
}
