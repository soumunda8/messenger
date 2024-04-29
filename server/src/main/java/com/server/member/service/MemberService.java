package com.server.member.service;

import com.server.member.domain.LoginResponse;
import com.server.member.domain.MemberDAO;
import com.server.member.repository.MemberMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService implements UserDetailsService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /*public MemberDAO checkUser(MemberDAO memberDAO) throws Exception {
        return memberMapper.checkUser(memberDAO);
    }*/

    public int registerMember(MemberDAO memberDAO) throws Exception {
        memberDAO.setPw(bCryptPasswordEncoder.encode(memberDAO.getPw()));
        return memberMapper.registerMember(memberDAO);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDAO member = memberMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        /*
        return User.builder()
                .username(member.getId())
                .password(member.getPw())  // DB에 저장된 해시된 패스워드
                .authorities("ROLE_USER")  // 실제 구현에 따라 권한 정보를 설정
                .build();
        */
        return new LoginResponse(
                member.getId(),
                member.getPw(),
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                member.getUsernm()
        );
    }

}