package com.navi.assignment2.repository;

import com.navi.assignment2.entity.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface EndpointRepository extends JpaRepository<Endpoint, Long> {
    @Query(value = "SELECT user_id, count(user_id) FROM endpoints WHERE user_id IS NOT NULL"
            + " AND created_at >= current_date - 7"
            + " GROUP BY user_id"
            + " ORDER BY count(user_id) desc"
            , nativeQuery = true)
    List<Map<Long, Integer>> getTopUsers();
}
