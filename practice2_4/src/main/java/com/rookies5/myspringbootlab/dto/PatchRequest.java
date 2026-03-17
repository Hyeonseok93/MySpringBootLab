package com.rookies5.myspringbootlab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchRequest {
    private String title;
    private String author;
    private String isbn;
    private Integer price;
    private LocalDate publishDate;
}
