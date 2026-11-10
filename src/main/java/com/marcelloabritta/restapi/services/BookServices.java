package com.marcelloabritta.restapi.services;

import com.marcelloabritta.restapi.controllers.BookController;
import com.marcelloabritta.restapi.data.dto.v1.BookDTO;
import com.marcelloabritta.restapi.exception.RequiredObjectIsNullException;
import com.marcelloabritta.restapi.exception.ResourceNotFoundException;
import com.marcelloabritta.restapi.model.Book;
import com.marcelloabritta.restapi.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.marcelloabritta.restapi.mapper.ObjectMapper.parseListObjects;
import static com.marcelloabritta.restapi.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookServices {
    private final BookRepository repository;

    public BookServices(BookRepository repository) {
        this.repository = repository;
    }

    public BookDTO findById(Long id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this Id."));
        var dto = parseObject(entity, BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public List<BookDTO> findAll() {
        var books = (parseListObjects(repository.findAll(), BookDTO.class));
        books.forEach(this::addHateoasLinks);
        return books;
    }

    public BookDTO create(BookDTO book) {
        if (book == null) throw new RequiredObjectIsNullException();

        var entity = parseObject(book, Book.class);
        entity.setId(null);
        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public BookDTO update(Long id, BookDTO book) {
        if (book == null) throw new RequiredObjectIsNullException();

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this Id."));

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this Id."));

        repository.delete(entity);
    }

    private void addHateoasLinks(BookDTO dto) {
        dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(BookController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
    }
}
