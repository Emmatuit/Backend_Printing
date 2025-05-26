package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Features.ClickedProductHistory;
import com.example.demo.model.UserEntity;

@Repository
public interface ClickedProductHistoryRepository extends JpaRepository<ClickedProductHistory, Long> {

	List<ClickedProductHistory> findBySessionId(String sessionId);

	List<ClickedProductHistory> findByUser(UserEntity user);

	List<ClickedProductHistory> findTop10BySessionIdOrderByClickedAtDesc(String sessionId);

	List<ClickedProductHistory> findTop10ByUserOrderByClickedAtDesc(UserEntity user);
}
