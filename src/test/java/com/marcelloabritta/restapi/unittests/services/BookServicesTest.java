package com.marcelloabritta.restapi.unittests.services;

import com.marcelloabritta.restapi.data.dto.v1.BookDTO;
import com.marcelloabritta.restapi.exception.RequiredObjectIsNullException;
import com.marcelloabritta.restapi.exception.ResourceNotFoundException;
import com.marcelloabritta.restapi.model.Book;
import com.marcelloabritta.restapi.repository.BookRepository;
import com.marcelloabritta.restapi.services.BookServices;
import com.marcelloabritta.restapi.unittests.mapper.mocks.MockBook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookServicesTest {

    private MockBook input = new MockBook();

    @InjectMocks
    private BookServices service;

    @Mock
    private BookRepository repository;

    @Test
    void testCreateWithNullBook() {
        var exception = assertThrows(RequiredObjectIsNullException.class,
                () -> service.create(null));

        String expectedMessage = "It is not allowed to persist a null object";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testUpdateWithNullBook() {
        var exception = assertThrows(RequiredObjectIsNullException.class,
                () -> service.update(1L, null));

        String expectedMessage = "It is not allowed to persist a null object";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void findById() {
        var book = input.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        var result = service.findById(1L);

        assertNotNull(result);

        assertHasAllLinks(result, 1L);

        assertEquals("Author Name1", result.getAuthor());
        assertEquals("Title1", result.getTitle());
        assertEquals(new BigDecimal("11"), result.getPrice());
        assertEquals(1L, result.getId());
        assertEquals(LocalDate.of(2025, 11, 10), result.getLaunchDate());
    }

    @Test
    void testFindByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> service.findById(1L));

        assertEquals("No record found for this Id.", exception.getMessage());
    }

    @Test
    void delete() {
        var book = input.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        service.delete(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(book);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testDeleteWithUnknownId() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).delete(any(Book.class));
        assertEquals("No record found for this Id.", exception.getMessage());
    }

    @Test
    void findAll() {
        List<Book> books = input.mockEntityList();
        when(repository.findAll()).thenReturn(books);

        var result = service.findAll();

        assertNotNull(result);
        assertEquals(books.size(), result.size());

        var bookOne = result.get(1);
        assertHasAllLinks(bookOne, 1L);

        assertEquals("Author Name1", bookOne.getAuthor());
        assertEquals("Title1", bookOne.getTitle());
        assertEquals(new BigDecimal("11"), bookOne.getPrice());
        assertEquals(1L, bookOne.getId());
        assertEquals(LocalDate.of(2025, 11, 10), bookOne.getLaunchDate());


        var bookFour = result.get(4);
        assertHasAllLinks(bookFour, 4L);
        assertEquals("Author Name4", bookFour.getAuthor());
        assertEquals("Title4", bookFour.getTitle());
        assertEquals(new BigDecimal("11"), bookFour.getPrice());
        assertEquals(4L, bookFour.getId());
        assertEquals(LocalDate.of(2025, 11, 10), bookFour.getLaunchDate());

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update() {
        var book = input.mockEntity(1);
        var dto = input.mockDTO(1);
        dto.setAuthor("Update author");
        dto.setTitle("Update title");
        dto.setPrice(new BigDecimal("15"));
        dto.setLaunchDate(LocalDate.of(2026, 1, 1));


        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(book);

        var result = service.update(1L, dto);

        assertNotNull(result);
        assertHasAllLinks(result, 1L);
        assertEquals("Update author", result.getAuthor());
        assertEquals("Update title", result.getTitle());
        assertEquals(new BigDecimal("15"), result.getPrice());
        assertEquals(1L, result.getId());
        assertEquals(LocalDate.of(2026, 1, 1), result.getLaunchDate());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(book);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testUpdateWithUnknownId() {
        var dto = input.mockDTO(1);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, dto));

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any(Book.class));
        assertEquals("No record found for this Id.", exception.getMessage());
    }

    @Test
    void create() {
        var dto = input.mockDTO(1);
        var entity = input.mockEntity(1);
        var captor = ArgumentCaptor.forClass(Book.class);
        when(repository.save(any(Book.class))).thenReturn(entity);

        var result = service.create(dto);

        assertNotNull(result);
        assertHasAllLinks(result, 1L);
        assertEquals("Author Name1", result.getAuthor());
        assertEquals("Title1", result.getTitle());
        assertEquals(new BigDecimal("11"), result.getPrice());
        assertEquals(1L, result.getId());
        assertEquals(LocalDate.of(2025, 11, 10), result.getLaunchDate());

        verify(repository, times(1)).save(captor.capture());
        var savedEntity = captor.getValue();

        assertNull(savedEntity.getId());
        verifyNoMoreInteractions(repository);
    }

    private void assertHasLink(BookDTO dto, String rel, String hrefEnd, String type) {
        assertTrue(dto.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals(rel)
                        && link.getHref().endsWith(hrefEnd)
                        && type.equals(link.getType()))
        );
    }


    private void assertHasAllLinks(BookDTO dto, Long id) {
        assertHasLink(dto, "self", "/api/v1/books/" + id, "GET");
        assertHasLink(dto, "findAll", "/api/v1/books", "GET");
        assertHasLink(dto, "create", "/api/v1/books", "POST");
        assertHasLink(dto, "update", "/api/v1/books/" + id, "PUT");
        assertHasLink(dto, "delete", "/api/v1/books/" + id, "DELETE");
    }
}
