package com.marcelloabritta.restapi.controllers;

import com.marcelloabritta.restapi.data.dto.v1.BookDTO;
import com.marcelloabritta.restapi.services.BookServices;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Page<BookDTO> findAll(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                 @RequestParam(value = "size", defaultValue = "12") @Min(1) @Max(50) Integer size,
                                 @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, "title"));
        return service.findAll(pageable);
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
