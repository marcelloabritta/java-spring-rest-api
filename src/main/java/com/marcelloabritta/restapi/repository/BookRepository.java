package com.marcelloabritta.restapi.repository;

import com.marcelloabritta.restapi.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
