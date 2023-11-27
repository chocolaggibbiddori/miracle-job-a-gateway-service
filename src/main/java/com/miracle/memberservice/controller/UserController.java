package com.miracle.memberservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/v1/user")
public class UserController {
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    // [임시] 일반공고 상세페이지 이동
    @GetMapping("/normalPost")
    public String mzPostDatail(){ return "user/normal-post"; }

    // [임시] 이력서 생성 폼으로 이동
    @GetMapping("/resume")
    public String createResume(){ return "user/resumeForm"; }

    // [임시] 이력서 목록으로 이동
    @GetMapping("/resumeList")
    public String resumeList(){ return "user/resumeList"; }

    // [임시] 자소서 목록으로 이동
    @GetMapping("/cover-letter")
    public String covetLetterList(){ return "user/cover-letter"; }

    @GetMapping("/coverLetterForm")
    public String createCoverLetter(){ return "user/coverLetterForm"; }
}
