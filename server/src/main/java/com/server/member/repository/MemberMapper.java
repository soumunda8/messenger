package com.server.member.repository;

import com.server.member.domain.MemberDAO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    Optional<MemberDAO> findByUsername(String id);
    int registerMember(MemberDAO memberDAO) throws Exception;

}