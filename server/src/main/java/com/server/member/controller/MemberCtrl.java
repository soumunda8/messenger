package com.server.member.controller;

import com.server.member.entity.MemberDAO;
import com.server.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MemberCtrl {

    @Autowired
    HttpSession session;

    @Autowired
    private MemberService memberService;

    @PostMapping("/login")
    public String loginPro(@RequestBody MemberDAO memberDAO) throws Exception {
        MemberDAO login = memberService.checkUser(memberDAO);
        if (login != null) {
            session.setAttribute("userId", login.getId());
            session.setAttribute("userNm", login.getUsernm());
            //return "true";
            return login.getUsernm();   // 임시
        } else {
            return "fail";
        }
    }

    @GetMapping("/logout")
    public String logoutPro() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>");
        System.out.println("로그아웃 들어옴");
        System.out.println(">>>>>>>>>>>>>>>>>>>>");
        session.invalidate();
        return "redirect:/";
    }

}