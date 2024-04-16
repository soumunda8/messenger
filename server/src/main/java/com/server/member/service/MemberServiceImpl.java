package com.server.member.service;

import com.server.member.entity.MemberDAO;
import com.server.member.persistence.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public MemberDAO checkUser(MemberDAO memberDAO) throws Exception {
        return memberMapper.checkUser(memberDAO);
    }

}