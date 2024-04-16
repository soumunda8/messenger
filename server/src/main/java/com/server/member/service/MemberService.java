package com.server.member.service;

import com.server.member.entity.MemberDAO;

public interface MemberService {

    public MemberDAO checkUser(MemberDAO memberDAO) throws Exception;

}