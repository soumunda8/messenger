package com.server.member.repository;

import com.server.member.domain.MemberDAO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    public MemberDAO checkUser(MemberDAO memberDAO) throws Exception;

}