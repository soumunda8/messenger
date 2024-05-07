package com.server.member.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class MemberSecurity extends User  {

    private String userId;
    private String userNm;
    private String accessToken;

    public MemberSecurity(String userId, String password, Collection<? extends GrantedAuthority> authorities, String userNm, String accessToken) {
        super(userId, password, authorities);
        this.userId = userId;
        this.userNm = userNm;
        this.accessToken = accessToken;
    }

}