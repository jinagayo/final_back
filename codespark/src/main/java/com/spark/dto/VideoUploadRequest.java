package com.spark.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoUploadRequest {
    private String title;
    private String key;       // S3 저장 key
    private String classId;     // 해당 강의 ID
    private String detail;
    private int duration;      // 영상 길이 (초 단위)
}