package com.naeggeodo.controller;

import com.naeggeodo.dto.ReportDTO;
import com.naeggeodo.entity.post.Report;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.ReportRepository;
import com.naeggeodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    @Transactional
    @PostMapping(value = "/report",produces = "application/json")
    public String saveReport(@RequestBody ReportDTO reportDTO){
        Users user = userRepository.getById(reportDTO.getUser_id());
        Report report = Report.create(user,reportDTO.getContents(),reportDTO.getType());
        reportRepository.save(report);
        return "asd";
    }
}
