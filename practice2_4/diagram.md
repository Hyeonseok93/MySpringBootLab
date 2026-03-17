# 프로젝트 구조 분석: MySpringBootLab (Book Management System)

이 문서는 제공된 UML 다이어그램을 바탕으로 한 Spring Boot 애플리케이션의 아키텍처 구조입니다.

## 1. Mermaid 클래스 다이어그램
```mermaid
classDiagram
    package "com.mookies7.myspringbootlab" {
        package "entity" {
            class Book {
                - Long id
                - String title
                - String author
                - String isbn
                - Integer price
                - LocalDate publishDate
                - BookDetail bookDetail
            }
            class BookDetail {
                - Long id
                - String description
                - String language
                - Integer pageCount
                - String publisher
                - String coverImageUrl
                - String edition
                - Book book
            }
        }

        package "repository" {
            class BookRepository {
                <<interface>>
                +findAllByTitleContainingIgnoreCase(String): List~Book~
                +findByTitleContainingIgnoreCase(String): List~Book~
                +findByIsbn(String): Optional~Book~
                +existsByIsbn(String): boolean
            }
            class BookDetailRepository {
                <<interface>>
                +findById(Long): Optional~BookDetail~
            }
        }

        package "service" {
            class BookService {
                - BookRepository bookRepository
                - BookDetailRepository bookDetailRepository
                +getAllBooks(): List~Response~
                +getBookById(Long): Response
                +getBookByIsbn(String): Response
                +createBook(Request): Response
                +updateBook(Long, Request): Response
                +deleteBook(Long): void
            }
        }

        package "controller" {
            class BookController {
                - BookService bookService
                +getAllBooks(): ResponseEntity
                +getBookById(Long): ResponseEntity
                +createBook(Request): ResponseEntity
                +updateBook(Long, Request): ResponseEntity
                +deleteBook(Long): ResponseEntity
            }
        }

        package "dto" {
            class BookDTO {
                <<static>> Request
                <<static>> Response
            }
            class BookDetailDTO {
                <<static>> Request
                <<static>> Response
            }
        }

        package "exception" {
            class BusinessException {
                - String message
                - HttpStatus httpStatus
            }
        }
    }

    %% Relationships
    Book "1" -- "1" BookDetail : OneToOne (mappedBy = "book")
    BookRepository ..> Book : manages
    BookDetailRepository ..> BookDetail : manages
    BookService --> BookRepository : injects
    BookService --> BookDetailRepository : injects
    BookController --> BookService : injects
    BookController ..> BookDTO : uses
    BookService ..> BookDTO : uses
