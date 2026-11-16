package com.marcelloabritta.restapi.integrationtests.controllers.cors.withjson;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marcelloabritta.restapi.config.TestConfigs;
import com.marcelloabritta.restapi.integrationtests.dto.BookDTO;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerCorsTest extends AbstractIntegrationTest {
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
    void createWithWrongOriginTest() throws JsonProcessingException {
        mockBook();

        RequestSpecification wrongOriginSpecification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_DENIED)
                .setBasePath("/api/v1/books")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(wrongOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .pathParam("id", book.getId())
                .when()
                .put("/{id}")
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();

        assertEquals("Invalid CORS request", content);
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
        assertEquals(new BigDecimal("89.90"), createdBook.getPrice());
    }

    @Test
    @Order(4)
    void findByIdWithWrongOriginTest() throws JsonProcessingException {
        RequestSpecification wrongOriginSpecification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_DENIED)
                .setBasePath("/api/v1/books")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
        var content = given(wrongOriginSpecification)
                .pathParam("id", book.getId())
                .when()
                .get("/{id}")
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();

        assertEquals("Invalid CORS request", content);
    }


    private void mockBook() {
        book.setAuthor("Martin Fowler");
        book.setTitle("Refactoring");
        book.setLaunchDate(LocalDate.of(2018, 11, 20));
        book.setPrice(new BigDecimal("89.90"));
    }
}

