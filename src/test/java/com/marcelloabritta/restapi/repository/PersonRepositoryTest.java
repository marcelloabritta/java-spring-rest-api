package com.marcelloabritta.restapi.repository;

import com.marcelloabritta.restapi.integrationtests.testcontainers.AbstractIntegrationTest;
import com.marcelloabritta.restapi.model.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {
    @Autowired
    PersonRepository repository;

    private static Person person;

    @BeforeAll
    static void setUp() {
        person = new Person();
    }


    @Test
    @Order(1)
    void findPeopleByName() {
        Pageable pageable = PageRequest.of(
                0,
                12,
                Sort.by(Sort.Direction.ASC, "firstName"));

        person = repository.findPeopleByName("nelson", pageable).getContent().getFirst();

        assertNotNull(person);
        assertEquals("nelson", person.getFirstName());
        assertEquals("Mandela", person.getLastName());
        assertEquals("Male", person.getGender());
        assertEquals("Mvezo - South Africa", person.getAddress());
        assertTrue(person.getEnabled());
    }

    @Test
    @Order(2)
    void disablePerson() {
        Long id = person.getId();
        repository.disablePerson(id);

        var result = repository.findById(id);
        person = result.get();

        assertNotNull(person);
        assertEquals("nelson", person.getFirstName());
        assertEquals("Mandela", person.getLastName());
        assertEquals("Male", person.getGender());
        assertEquals("Mvezo - South Africa", person.getAddress());
        assertFalse(person.getEnabled());
    }
}