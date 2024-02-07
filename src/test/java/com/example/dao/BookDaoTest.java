package com.example.dao;

import com.example.Utils.BookUtil;
import com.example.common.ObjectTranslator;
import com.example.dao.entity.AuditEntity;
import com.example.dao.entity.BookEntity;
import com.example.dao.repository.AuditRepository;
import com.example.dao.repository.BookRepository;
import com.example.dto.AuditDto;
import com.example.dto.BookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BookDaoTest {
    @InjectMocks
    private BookDao bookDao;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ObjectTranslator translator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBooks() {
        BookEntity bookEntity = BookUtil.getBookEntity();
        BookDto bookDto = BookUtil.getBookDto();
        when(bookRepository.findAll())
                .thenReturn(Flux.just(bookEntity));

        when(translator.translate(any(BookEntity.class), Mockito.eq(BookDto.class)))
                .thenReturn(bookDto);

        Flux<BookDto> result = bookDao.getAllBooks();

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testSearchByTitle() {
        BookEntity bookEntity = BookUtil.getBookEntity();
        BookDto bookDto = BookUtil.getBookDto();
        String title = "Spring";

        when(bookRepository.findByTitleContainingIgnoreCase(title))
                .thenReturn(Flux.just(bookEntity));

        when(translator.translate(any(BookEntity.class), Mockito.eq(BookDto.class)))
                .thenReturn(bookDto);

        Flux<BookDto> result = bookDao.searchByTitle(title);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testSearchByAuthor() {
        BookEntity bookEntity = BookUtil.getBookEntity();
        BookDto bookDto = BookUtil.getBookDto();
        String author = "Author";

        when(bookRepository.findByAuthorsContainingIgnoreCase(author))
                .thenReturn(Flux.just(bookEntity));

        when(translator.translate(any(BookEntity.class), Mockito.eq(BookDto.class)))
                .thenReturn(bookDto);


        Flux<BookDto> result = bookDao.searchByAuthor(author);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testFindByBookId() {

        String bookId = "123";
        BookEntity bookEntity = BookUtil.getBookEntity();
        BookDto bookDto = BookUtil.getBookDto();
        when(bookRepository.findByBookId(bookId))
                .thenReturn(Mono.just(bookEntity));

        when(translator.translate(any(BookEntity.class), Mockito.eq(BookDto.class)))
                .thenReturn(bookDto);


        Mono<BookDto> result = bookDao.findByBookId(bookId);


        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testSave() {

        BookEntity bookEntity = BookUtil.getBookEntity();
        BookDto bookDto = BookUtil.getBookDto();

        when(translator.translate(bookDto, BookEntity.class))
                .thenReturn(bookEntity);

        when(bookRepository.save(bookEntity))
                .thenReturn(Mono.just(bookEntity));

        when(translator.translate(bookEntity, BookDto.class))
                .thenReturn(bookDto);


        Mono<BookDto> result = bookDao.save(bookDto);


        StepVerifier.create(result)
                .expectNext(bookDto)
                .verifyComplete();
    }

    @Test
    void testDeleteBook() {
        String bookId = "123";

        when(bookRepository.deleteByBookId(bookId))
                .thenReturn(Mono.empty());


        Mono<Void> result = bookDao.deleteBook(bookId);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testSearchByTitleAndAuthor() {

        String title = "Spring";
        String author = "Author";
        BookEntity bookEntity = BookUtil.getBookEntity();
        BookDto bookDto = BookUtil.getBookDto();

        when(bookRepository.findByTitleIgnoreCaseContainingAndAuthorsIgnoreCaseContaining(title, author))
                .thenReturn(Flux.just(bookEntity));

        when(translator.translate(any(BookEntity.class), Mockito.eq(BookDto.class)))
                .thenReturn(bookDto);


        Flux<BookDto> result = bookDao.searchByTitleAndAuthor(title, author);


        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

}
