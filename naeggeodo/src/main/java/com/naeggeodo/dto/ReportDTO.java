package com.naeggeodo.dto;

import com.naeggeodo.entity.post.ReportType;
import lombok.Data;

@Data
public class ReportDTO {
    private String user_id;
    private String contents;
    private ReportType type;
}
