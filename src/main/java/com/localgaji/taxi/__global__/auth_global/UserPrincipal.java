package com.localgaji.taxi.__global__.auth_global;

import java.security.Principal;

public class UserPrincipal implements Principal {
    private final Long userId;

    public UserPrincipal(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

}
