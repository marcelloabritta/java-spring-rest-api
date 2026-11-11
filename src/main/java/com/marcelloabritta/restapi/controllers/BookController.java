package com.marcelloabritta.restapi.controllers;

import com.marcelloabritta.restapi.data.dto.v1.BookDTO;
import com.marcelloabritta.restapi.services.BookServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookServices service;

    public BookController(BookServices service) {

        this.service = service;
    }

    @GetMapping
    public List<BookDTO> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public BookDTO findById(@PathVariable("id") Long id) {

        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<BookDTO> create(@RequestBody BookDTO book) {
        var dto = service.create(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public BookDTO update(@PathVariable("id") Long id, @RequestBody BookDTO book) {
        return service.update(id, book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
