package com.miracle.memberservice.service;

import com.miracle.memberservice.dto.request.JobRequestDto;
import com.miracle.memberservice.dto.request.StackRequestDto;
import com.miracle.memberservice.dto.response.*;
import com.miracle.memberservice.util.ApiResponseToList;
import com.miracle.memberservice.util.Const;
import com.miracle.memberservice.util.PageMoveWithMessage;
import com.miracle.memberservice.util.ServiceCall;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {
    public Map<String, List<?>> getAllJobsAndStacks(HttpSession session) {
        ApiResponse response = ServiceCall.get(session, "admin", "/admin/jobstacks");

        if(response.getHttpStatus()!=200) return null;

        LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) response.getData();

        List<StackResponseDto> stacks = ApiResponseToList.stacks(data.get("stacks"));
        List<JobResponseDto> jobs = ApiResponseToList.jobs(data.get("jobs"));

        Map<String, List<?>> stacksAndJobs = new HashMap<>();

        stacksAndJobs.put("stacks", stacks);
        stacksAndJobs.put("jobs", jobs);

        return stacksAndJobs;
    }

    public List<JobResponseDto> getJobs(HttpSession session, List<Integer> jobs){
        ApiResponse response = ServiceCall.post(session, new JobRequestDto(jobs), Const.RequestHeader.ADMIN, "/admin/jobs");
        if (response.getData() instanceof Boolean) return null;

        return ApiResponseToList.jobs(response.getData());
    }

    public List<StackResponseDto> getStacks(HttpSession session, List<Integer> stacks){
        ApiResponse response = ServiceCall.post(session, new StackRequestDto(stacks), Const.RequestHeader.ADMIN, "/admin/stacks");
        if (response.getData() instanceof Boolean) return null;

        return ApiResponseToList.stacks(response.getData());
    }

    public List<JobResponseDto> getAllJobs(HttpSession session){
        ApiResponse response = ServiceCall.get(session, Const.RequestHeader.ADMIN, "/admin/jobs");
        return ApiResponseToList.jobs(response.getData());
    }

    public PageMoveWithMessage getUserList(HttpSession session, int strNum, int endNum) {
        ApiResponse response = ServiceCall.getParamList(session, Const.RequestHeader.USER, "/user", strNum, endNum);
        if (response.getHttpStatus() != 200) return new PageMoveWithMessage("index", response.getMessage());
        List<List<UserListResponseDto>> userList = ApiResponseToList.userList(response.getData());

        return new PageMoveWithMessage("admin/userList", userList);
    }

    public PageMoveWithMessage getCompanyList(HttpSession session, int strNum, int endNum) {
        ApiResponse response = ServiceCall.getParamList(session, Const.RequestHeader.COMPANY, "/company/list", strNum, endNum, false);
        if (response.getHttpStatus() != 200) return new PageMoveWithMessage("index", response.getMessage());
        List<List<CompanyListResponseDto>> companyList = ApiResponseToList.companyList(response.getData());

        return new PageMoveWithMessage("admin/companyList", companyList);
    }
}
