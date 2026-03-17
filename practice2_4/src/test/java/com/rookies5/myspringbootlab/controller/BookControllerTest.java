package com.rookies5.myspringbootlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rookies5.myspringbootlab.dto.BookDTO;
import com.rookies5.myspringbootlab.exception.BusinessException;
import com.rookies5.myspringbootlab.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Test
    @DisplayName("도서 등록 API 성공 테스트")
    void createBook_Api_Success() throws Exception {
        // given
        BookDTO.Request request = BookDTO.Request.builder()
                .title("테스트 제목")
                .author("테스트 저자")
                .isbn("978-3-16-148410-0")
                .price(10000)
                .build();

        BookDTO.Response response = BookDTO.Response.builder()
                .id(1L)
                .title("테스트 제목")
                .build();

        given(bookService.createBook(any(BookDTO.Request.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("테스트 제목"));
    }

    @Test
    @DisplayName("도서 등록 API 유효성 검사 실패 (가격 음수)")
    void createBook_Api_Validation_Failure() throws Exception {
        // given
        BookDTO.Request request = BookDTO.Request.builder()
                .title("제목")
                .author("저자")
                .isbn("123")
                .price(-500) // 잘못된 값
                .build();

        // when & then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ID로 도서 조회 성공")
    void getBookById_Api_Success() throws Exception {
        // given
        BookDTO.Response response = BookDTO.Response.builder()
                .id(1L)
                .title("제목")
                .build();

        given(bookService.getBookById(1L)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"));
    }

    @Test
    @DisplayName("비즈니스 예외 발생 시 ProblemDetail 응답 확인")
    void getBookById_Api_BusinessException() throws Exception {
        // given
        given(bookService.getBookById(99L))
                .willThrow(new BusinessException("도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.detail").value("도서를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("전체 업데이트 API 호출")
    void updateBook_Api_Success() throws Exception {
        // given
        BookDTO.Request updateRequest = BookDTO.Request.builder()
                .title("수정된 제목")
                .author("테스트 저자")
                .isbn("978-3-16-148410-0")
                .price(10000)
                .build();

        BookDTO.Response response = BookDTO.Response.builder()
                .id(1L)
                .title("수정된 제목")
                .build();

        given(bookService.updateBook(eq(1L), any(BookDTO.Request.class))).willReturn(response);

        // when & then
        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"));
    }
}
