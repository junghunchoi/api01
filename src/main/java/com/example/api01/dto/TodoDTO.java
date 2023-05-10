package com.example.api01.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoDTO {

    private Long tno;
    private String title;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy--MM--dd", timezone = "Asia/seoul")
    private LocalDate dueDate;

    private String writer;
    private boolean complete;

}
