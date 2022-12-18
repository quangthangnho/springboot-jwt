package com.learnudemy.enums;

import com.learnudemy.constant.Authority;
import lombok.Getter;

@Getter
public enum Role {

    ROLE_USER(Authority.USER_AUTHORITIES),
    ROLE_HR(Authority.HR_AUTHORITIES),
    ROLE_MANAGER(Authority.MANAGER_AUTHORITIES),
    ROLE_ADMIN(Authority.ADMIN_AUTHORITIES),
    ROLE_SUPPER_ADMIN(Authority.SUPER_ADMIN_AUTHORITIES);

    private String[] authorities;

    Role(String[] authorities) {
        this.authorities = authorities;
    }
}
