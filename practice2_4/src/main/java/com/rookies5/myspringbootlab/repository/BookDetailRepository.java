package com.rookies5.myspringbootlab.repository;

import com.rookies5.myspringbootlab.entity.BookDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookDetailRepository extends JpaRepository<BookDetail, Long> {
    Optional<BookDetail> findByBookId(Long bookId);

    @EntityGraph(attributePaths = {"book"})
    @Query("select bd from BookDetail bd where bd.id = :id")
    Optional<BookDetail> findByIdWithBook(Long id);

    List<BookDetail> findByPublisher(String publisher);
}
