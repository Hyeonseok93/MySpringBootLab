package com.rookies5.myspringbootlab.repository;

import com.rookies5.myspringbootlab.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("도서 등록 테스트")
    void testCreateBook() {
        Book book = Book.builder()
                .title("스프링 부트 입문")
                .author("홍길동")
                .isbn("9788956746425")
                .price(30000)
                .publishDate(LocalDate.of(2025, 5, 7))
                .build();

        Book savedBook = bookRepository.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("스프링 부트 입문");
    }

    @Test
    @DisplayName("ISBN으로 도서 조회 테스트")
    void testFindByIsbn() {
        String isbn = "9788956746432";
        bookRepository.save(Book.builder().title("JPA 프로그래밍").author("박둘리").isbn(isbn).build());

        Optional<Book> foundBook = bookRepository.findByIsbn(isbn);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getAuthor()).isEqualTo("박둘리");
    }

    @Test
    @DisplayName("저자명으로 도서 목록 조회 테스트")
    void testFindByAuthor() {
        bookRepository.save(Book.builder().title("Book 1").author("홍길동").build());
        bookRepository.save(Book.builder().title("Book 2").author("홍길동").build());

        List<Book> books = bookRepository.findByAuthor("홍길동");

        assertThat(books).hasSize(2);
        assertThat(books.get(0).getAuthor()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("도서 정보 수정 테스트")
    void testUpdateBook() {
        Book book = bookRepository.save(Book.builder().title("원래 제목").build());

        book.setTitle("수정된 제목");
        Book updatedBook = bookRepository.save(book);

        assertThat(updatedBook.getTitle()).isEqualTo("수정된 제목");
    }

    @Test
    @DisplayName("도서 삭제 테스트")
    void testDeleteBook() {
        Book book = bookRepository.save(Book.builder().title("삭제할 도서").build());

        bookRepository.delete(book);
        Optional<Book> deletedBook = bookRepository.findById(book.getId());

        assertThat(deletedBook).isEmpty();
    }
}
