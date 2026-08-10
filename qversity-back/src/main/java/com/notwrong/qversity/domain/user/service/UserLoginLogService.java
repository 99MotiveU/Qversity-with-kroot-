package com.notwrong.qversity.domain.user.service;

import com.notwrong.qversity.domain.user.entity.UserLoginLogs;

public interface UserLoginLogService {

    UserLoginLogs recordLogin(Long socialId);
}
