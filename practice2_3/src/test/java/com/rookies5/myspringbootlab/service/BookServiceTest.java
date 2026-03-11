package com.rookies5.myspringbootlab.service;

import com.rookies5.myspringbootlab.dto.BookDTO;
import com.rookies5.myspringbootlab.entity.Book;
import com.rookies5.myspringbootlab.exception.BusinessException;
import com.rookies5.myspringbootlab.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("모든 도서 목록 조회 시 DTO 리스트로 변환되어야 함")
    void getAllBooks_Success() {
        // given
        given(bookRepository.findAll()).willReturn(List.of(
                Book.builder().id(1L).title("Book 1").build(),
                Book.builder().id(2L).title("Book 2").build()
        ));

        // when
        List<BookDTO.BookResponse> responses = bookService.getAllBooks();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("Book 1");
    }

    @Test
    @DisplayName("ID로 도서 조회 성공")
    void getBookById_Success() {
        // given
        Long bookId = 1L;
        given(bookRepository.findById(bookId)).willReturn(Optional.of(
                Book.builder().id(bookId).title("Book 1").build()
        ));

        // when
        BookDTO.BookResponse response = bookService.getBookById(bookId);

        // then
        assertThat(response.getId()).isEqualTo(bookId);
        assertThat(response.getTitle()).isEqualTo("Book 1");
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 404 예외가 발생해야 함")
    void getBookById_NotFound_ThrowsException() {
        // given
        Long bookId = 99L;
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.getBookById(bookId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("찾을 수 없습니다")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("새로운 도서 등록 성공")
    void createBook_Success() {
        // given
        BookDTO.BookCreateRequest request = BookDTO.BookCreateRequest.builder()
                .title("New Book")
                .author("Author")
                .isbn("ISBN123")
                .price(20000)
                .build();
        
        given(bookRepository.findByIsbn(request.getIsbn())).willReturn(Optional.empty());
        given(bookRepository.save(any(Book.class))).willReturn(request.toEntity());

        // when
        BookDTO.BookResponse response = bookService.createBook(request);

        // then
        assertThat(response.getTitle()).isEqualTo("New Book");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("중복된 ISBN으로 등록 시 400 예외가 발생해야 함")
    void createBook_DuplicateIsbn_ThrowsException() {
        // given
        BookDTO.BookCreateRequest request = BookDTO.BookCreateRequest.builder()
                .isbn("DUPE123")
                .build();
        
        given(bookRepository.findByIsbn("DUPE123")).willReturn(Optional.of(new Book()));

        // when & then
        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 존재하는 ISBN")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("제목만 수정했을 때 다른 필드는 유지되어야 함 (부분 업데이트)")
    void updateBook_PartialUpdate_Success() {
        // given
        Long bookId = 1L;
        Book existBook = Book.builder()
                .id(bookId)
                .title("Old Title")
                .author("Old Author")
                .price(10000)
                .build();
        
        BookDTO.BookUpdateRequest updateRequest = BookDTO.BookUpdateRequest.builder()
                .title("New Title")
                .build();

        given(bookRepository.findById(bookId)).willReturn(Optional.of(existBook));
        given(bookRepository.save(any(Book.class))).willReturn(existBook);

        // when
        BookDTO.BookResponse response = bookService.updateBook(bookId, updateRequest);

        // then
        assertThat(response.getTitle()).isEqualTo("New Title");
        assertThat(response.getAuthor()).isEqualTo("Old Author"); // 저자는 유지
        assertThat(response.getPrice()).isEqualTo(10000); // 가격 유지
    }

    @Test
    @DisplayName("도서 삭제 성공")
    void deleteBook_Success() {
        // given
        Long bookId = 1L;
        Book book = Book.builder().id(bookId).build();
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // when
        bookService.deleteBook(bookId);

        // then
        verify(bookRepository).delete(book);
    }
}
