package com.spark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    @Size(max = 1000, message = "댓글은 1000자 이내로 작성해주세요.")
    private String content;
    
    private Integer parentCommentId; // 대댓글 작성 시 부모 댓글 ID
}
