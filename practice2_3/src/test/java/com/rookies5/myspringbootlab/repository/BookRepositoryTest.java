package com.rookies5.myspringbootlab.repository;

import com.rookies5.myspringbootlab.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("1. 도서 저장 테스트")
    void testSaveBook() {
        // given
        Book book = Book.builder()
                .title("저장 테스트 도서")
                .author("저장 작가")
                .isbn("SAVE-12345")
                .price(10000)
                .publishDate(LocalDate.now())
                .build();

        // when
        Book savedBook = bookRepository.save(book);

        // then
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("저장 테스트 도서");
    }

    @Test
    @DisplayName("2. ISBN으로 도서 조회 테스트")
    void testFindByIsbn() {
        // given
        String isbn = "ISBN-UNIQUE-999";
        bookRepository.save(Book.builder().title("ISBN 테스트").author("작가").isbn(isbn).build());

        // when
        Optional<Book> foundBook = bookRepository.findByIsbn(isbn);

        // then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getIsbn()).isEqualTo(isbn);
    }

    @Test
    @DisplayName("3. 저자명으로 도서 목록 조회 테스트")
    void testFindByAuthor() {
        // given
        String testAuthor = "조회전용작가";
        bookRepository.save(Book.builder().title("Book 1").author(testAuthor).isbn("AUTH-1").build());
        bookRepository.save(Book.builder().title("Book 2").author(testAuthor).isbn("AUTH-2").build());

        // when
        List<Book> books = bookRepository.findByAuthor(testAuthor);

        // then
        assertThat(books).hasSize(2);
        assertThat(books).extracting("author").containsOnly(testAuthor);
    }

    @Test
    @DisplayName("4. 도서 정보 수정 테스트")
    void testUpdateBook() {
        // given
        Book book = bookRepository.save(Book.builder().title("수정 전 제목").author("작가").isbn("UP-123").build());

        // when
        book.setTitle("수정 후 제목");
        book.setPrice(50000);
        Book updatedBook = bookRepository.save(book);

        // then
        assertThat(updatedBook.getTitle()).isEqualTo("수정 후 제목");
        assertThat(updatedBook.getPrice()).isEqualTo(50000);
    }

    @Test
    @DisplayName("5. 도서 삭제 테스트")
    void testDeleteBook() {
        // given
        Book book = bookRepository.save(Book.builder().title("삭제할 도서").author("작가").isbn("DEL-123").build());
        Long id = book.getId();

        // when
        bookRepository.delete(book);
        Optional<Book> deletedBook = bookRepository.findById(id);

        // then
        assertThat(deletedBook).isEmpty();
    }

    @Test
    @DisplayName("6. 전체 도서 목록 조회 테스트")
    void testFindAll() {
        // given
        bookRepository.save(Book.builder().title("All 1").author("A").isbn("ALL-1").build());
        bookRepository.save(Book.builder().title("All 2").author("B").isbn("ALL-2").build());

        // when
        List<Book> allBooks = bookRepository.findAll();

        // then
        assertThat(allBooks.size()).isGreaterThanOrEqualTo(2);
    }
}
