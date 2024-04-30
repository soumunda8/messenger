package com.server.member.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class LoginResponse extends User {

    private String userid;
    private String userNm;
    private String token;

    public LoginResponse(String userid, String password, Collection<? extends GrantedAuthority> authorities, String userNm, String token) {
        super(userid, password, authorities);
        this.userid = userid;
        this.userNm = userNm;
        this.token = token;
    }

}