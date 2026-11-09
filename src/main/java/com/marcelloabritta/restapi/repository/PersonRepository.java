package com.marcelloabritta.restapi.repository;

import com.marcelloabritta.restapi.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
