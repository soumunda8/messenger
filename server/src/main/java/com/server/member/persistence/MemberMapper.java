package com.server.member.persistence;

import com.server.member.entity.MemberDAO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    public MemberDAO checkUser(MemberDAO memberDAO) throws Exception;

}