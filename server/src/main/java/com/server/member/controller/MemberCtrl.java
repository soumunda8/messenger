package com.server.member.controller;

import com.server.commmons.security.provider.JwtUtils;
import com.server.member.domain.LoginRequest;
import com.server.member.domain.LoginResponse;
import com.server.member.domain.MemberDAO;
import com.server.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> loginPro(@RequestBody MemberDAO memberDAO) throws Exception {

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberDAO.getId(), memberDAO.getPw())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (userDetails instanceof LoginResponse loginResponse) {
                // 토큰 생성
                String token = JwtUtils.generateToken(userDetails.getUsername());
                loginResponse.setAccessToken(token);

                return ResponseEntity.ok(loginResponse);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User details are not of type 'LoginResponse'");
            }

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }

    }

    /*@PostMapping("/logout")
    public boolean logoutPro() {
        session.invalidate();
        return true;
    }*/

}