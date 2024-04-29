package com.server.member.controller;

import com.server.commmons.security.provider.JwtUtils;
import com.server.member.domain.LoginRequest;
import com.server.member.domain.LoginResponse;
import com.server.member.domain.MemberDAO;
import com.server.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MemberCtrl {

    @Autowired
    HttpSession session;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/registration")
    public ResponseEntity<Integer> joinPro(@RequestBody MemberDAO member) throws Exception {
        int num = memberService.registerMember(member);
        return ResponseEntity.ok(num);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPro(@RequestBody LoginRequest loginRequest) throws Exception {
        System.out.println(">>>>>>>>>>>로그인 들어옴");
        System.out.println(loginRequest.getId());
        System.out.println(loginRequest.getPw());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPw())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtUtils.generateToken(authentication.getName());  // Assume JwtUtils class exists
        //LoginResponse loginResponse = new LoginResponse();
        //loginResponse.setToken(token);
        return ResponseEntity.ok(token);
        //return null;
        /*
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getId(),
                        loginRequest.getPw()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = "dummy-jwt-token-for-demo-purpose";  // 실제 토큰 생성 로직으로 대체 필요
        String userNm = userDetails.getUsername();  // 실제 이름을 UserDetails에서 추출, 적절한 메서드로 대체 필요

        LoginResponse response = new LoginResponse(loginRequest.getId(), userNm, token);

        return ResponseEntity.ok(response);
        */
        /*MemberDAO login = memberService.checkUser(memberDAO);
        if (login != null) {
            session.setAttribute("userId", login.getId());
            session.setAttribute("userNm", login.getUsernm());
            //return "true";
            return login.getUsernm();   // 임시
        } else {
            return "fail";
        }*/
    }

    @PostMapping("/logout")
    public boolean logoutPro() {
        session.invalidate();
        return true;
    }

}