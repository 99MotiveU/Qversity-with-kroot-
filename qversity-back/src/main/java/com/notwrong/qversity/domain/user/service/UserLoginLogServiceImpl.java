package com.notwrong.qversity.domain.user.service;

import com.notwrong.qversity.domain.user.entity.UserLoginLogs;
import com.notwrong.qversity.domain.user.repository.UserLoginLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserLoginLogServiceImpl implements UserLoginLogService{

    private final UserLoginLogsRepository userLoginLogsRepository;

    @Override
    public UserLoginLogs recordLogin(Long socialId) {
        UserLoginLogs loginLog = UserLoginLogs.builder()
                .socialId(socialId)
                .loginAt(LocalDateTime.now())
                .build();

        return userLoginLogsRepository.save(loginLog);
    }
}
