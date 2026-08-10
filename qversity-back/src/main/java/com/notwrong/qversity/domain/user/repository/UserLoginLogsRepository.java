package com.notwrong.qversity.domain.user.repository;

import com.notwrong.qversity.domain.user.entity.UserLoginLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginLogsRepository extends JpaRepository<UserLoginLogs, Long> {
}
