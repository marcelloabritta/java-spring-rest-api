package com.marcelloabritta.restapi.integrationtests.controllers.withjson;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marcelloabritta.restapi.config.TestConfigs;
import com.marcelloabritta.restapi.integrationtests.dto.BookDTO;
import com.marcelloabritta.restapi.integrationtests.dto.wrapper.json.WrapperBookDTO;
import com.marcelloabritta.restapi.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static BookDTO book;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new JavaTimeModule());

        book = new BookDTO();
    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockBook();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_FRONT)
                .setBasePath("/api/v1/books")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .body()
                .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertTrue(createdBook.getId() > 0);

        assertEquals("Martin Fowler", createdBook.getAuthor());
        assertEquals("Refactoring", createdBook.getTitle());
        assertEquals(LocalDate.of(2018, 11, 20), createdBook.getLaunchDate());
        assertEquals(new BigDecimal("89.90"), createdBook.getPrice());
    }

    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        book.setPrice(new BigDecimal("100.00"));
        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .pathParam("id", book.getId())
                .when()
                .put("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertTrue(createdBook.getId() > 0);

        assertEquals("Martin Fowler", createdBook.getAuthor());
        assertEquals("Refactoring", createdBook.getTitle());
        assertEquals(LocalDate.of(2018, 11, 20), createdBook.getLaunchDate());
        assertEquals(new BigDecimal("100.00"), createdBook.getPrice());
    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {
        var content = given(specification)
                .pathParam("id", book.getId())
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertTrue(createdBook.getId() > 0);

        assertEquals("Martin Fowler", createdBook.getAuthor());
        assertEquals("Refactoring", createdBook.getTitle());
        assertEquals(LocalDate.of(2018, 11, 20), createdBook.getLaunchDate());
        assertEquals(new BigDecimal("100.00"), createdBook.getPrice());
    }

    @Test
    @Order(4)
    void deleteTest() {
        given(specification)
                .pathParam("id", book.getId())
                .when()
                .delete("/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    void findAllTest() throws JsonProcessingException {
        var content = given(specification)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams("page", 2, "size", 4, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        WrapperBookDTO wrapper = objectMapper.readValue(content, WrapperBookDTO.class);
        List<BookDTO> books = wrapper.getContent();

        BookDTO bookOne = books.getFirst();

        assertTrue(bookOne.getId() > 0);

        assertEquals("Brian Goetz e Tim Peierls", bookOne.getAuthor());
        assertEquals(LocalDate.of(2017, 11, 7), bookOne.getLaunchDate());
        assertEquals("Java Concurrency in Practice", bookOne.getTitle());
        assertEquals(new BigDecimal("80.00"), bookOne.getPrice());

        BookDTO bookThree = books.get(2);

        assertTrue(bookThree.getId() > 0);

        assertEquals("Susan Cain", bookThree.getAuthor());
        assertEquals(LocalDate.of(2017, 11, 7), bookThree.getLaunchDate());
        assertEquals("O poder dos quietos", bookThree.getTitle());
        assertEquals(new BigDecimal("123.00"), bookThree.getPrice());
    }


    private void mockBook() {
        book.setAuthor("Martin Fowler");
        book.setTitle("Refactoring");
        book.setLaunchDate(LocalDate.of(2018, 11, 20));
        book.setPrice(new BigDecimal("89.90"));
    }
}

