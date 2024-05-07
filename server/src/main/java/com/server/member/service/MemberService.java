package com.server.member.service;

import com.server.member.domain.MemberDAO;
import com.server.member.domain.MemberSecurity;
import com.server.member.repository.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService implements UserDetailsService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /*public MemberDAO checkUser(MemberDAO memberDAO) throws Exception {
        return memberMapper.checkUser(memberDAO);
    }*/

    public Optional<MemberDAO> findByUsername(String username) throws Exception {
        return memberMapper.findByUsername(username);
    }

    public int registerMember(MemberDAO memberDAO) throws Exception {
        memberDAO.setPw(bCryptPasswordEncoder.encode(memberDAO.getPw()));
        return memberMapper.registerMember(memberDAO);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDAO member = memberMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new MemberSecurity(
                member.getId(),
                member.getPw(),
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                member.getUsernm(),
                ""
        );
    }

}