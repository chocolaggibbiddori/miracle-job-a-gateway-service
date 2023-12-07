package com.miracle.memberservice.controller;

import com.miracle.memberservice.dto.response.StackAndJobResponseDto;
import com.miracle.memberservice.service.AdminService;
import com.miracle.memberservice.util.PageMoveWithMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }
    @GetMapping("/users")
    private String userList(){ return "admin/userList"; }

    @GetMapping("/stacks")
    private String stackList(){ return "admin/stackList"; }

    @GetMapping("/stackList")
    private String stackList(HttpSession session, Model model){
        PageMoveWithMessage pmwm = adminService.getAllStack(session);
        List<StackAndJobResponseDto> data = (List<StackAndJobResponseDto>) pmwm.getData();

        model.addAttribute("totalStackList", data);
        return pmwm.getPageName();
    }

    @PostMapping("/stacks")
    private String modifyStack(@RequestParam Long stackId, @RequestParam String modifiedName, Model model, HttpSession session){
        PageMoveWithMessage pmwm = adminService.modifyStack(session, stackId, modifiedName);
        List<StackAndJobResponseDto> data = (List<StackAndJobResponseDto>) pmwm.getData();

        model.addAttribute("totalStackList", data);
        return pmwm.getPageName();
    }

    @GetMapping("/search/stack")
    private String searchStack(@RequestParam String stackName, Model model, HttpSession session){
        PageMoveWithMessage pmwm = adminService.searchStack(session, stackName);
        List<StackAndJobResponseDto> data = (List<StackAndJobResponseDto>) pmwm.getData();

        model.addAttribute("totalStackList", data);
        return pmwm.getPageName();
    }

    @GetMapping("/jobs")
    private String jobList(){ return "admin/jobList"; }

}
