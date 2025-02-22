package com.server.member.controller;

import com.server.commmons.security.provider.JwtUtils;
import com.server.member.domain.LoginRequest;
import com.server.member.domain.LoginResponse;
import com.server.member.domain.MemberDAO;
import com.server.member.domain.MemberStatus;
import com.server.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<String> loginPro(@RequestBody MemberDAO memberDAO) throws Exception {

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberDAO.getId(), memberDAO.getPw())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String token = JwtUtils.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }

    }

    @MessageMapping("/userState")
    @SendTo("/chat/send/status")
    public MemberStatus loginUser(@Payload MemberStatus status) {
        return new MemberStatus(status.getUserId(), true);
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> userInfo(@RequestHeader("Authorization") String authHeader, Principal principal) throws Exception {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header.");
        }

        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            String username = JwtUtils.getUsernameFromToken(token);

            Optional<MemberDAO> optionalMemberDAO = memberService.findByUsername(username);

            if(optionalMemberDAO.isPresent()) {
                MemberDAO userInfo = optionalMemberDAO.get();
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setUserId(username);
                loginResponse.setUserNm(userInfo.getUsernm());
                return ResponseEntity.ok(loginResponse);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User details has no Info");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }

    }

    /*@PostMapping("/logout")
    public boolean logoutPro() {
        session.invalidate();
        return true;
    }*/

}