package com.marcelloabritta.restapi.integrationtests.controllers.withxml;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marcelloabritta.restapi.config.TestConfigs;
import com.marcelloabritta.restapi.integrationtests.dto.AccountCredentialsDTO;
import com.marcelloabritta.restapi.integrationtests.dto.BookDTO;
import com.marcelloabritta.restapi.integrationtests.dto.TokenDTO;
import com.marcelloabritta.restapi.integrationtests.dto.wrapper.xml.PagedModelBook;
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
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static XmlMapper objectMapper;
    private static BookDTO book;
    private static TokenDTO tokenDto;

    @BeforeAll
    static void setUp() {
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        book = new BookDTO();
        tokenDto = new TokenDTO();
    }

    @Test
    @Order(0)
    void signin() throws JsonProcessingException {
        AccountCredentialsDTO credentials = new AccountCredentialsDTO("leandro", "admin123");

        var content = given()
                .basePath("auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(credentials)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        tokenDto = objectMapper.readValue(content, TokenDTO.class);

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_FRONT)
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDto.getAccessToken())
                .setBasePath("/api/v1/books")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        assertNotNull(tokenDto.getAccessToken());
        assertNotNull(tokenDto.getRefreshToken());
    }


    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockBook();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(objectMapper.writeValueAsString((book)))
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
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(objectMapper.writeValueAsString((book)))
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
                .accept(MediaType.APPLICATION_XML_VALUE)
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
                .accept(MediaType.APPLICATION_XML_VALUE)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    void findAllTest() throws JsonProcessingException {
        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .queryParams("page", 0, "size", 12, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);
        List<BookDTO> books = wrapper.getContent();

        BookDTO bookOne = books.getFirst();

        assertTrue(bookOne.getId() > 0);

        assertEquals("Craig Larman", bookOne.getAuthor());
        assertEquals(LocalDate.of(1998, 12, 5), bookOne.getLaunchDate());
        assertEquals("Agile and Iterative Development: A Manager’s Guide", bookOne.getTitle());
        assertEquals(new BigDecimal("43.82"), bookOne.getPrice());

        BookDTO bookFive = books.get(4);

        assertTrue(bookFive.getId() > 0);

        assertEquals("Craig Larman", bookFive.getAuthor());
        assertEquals(LocalDate.of(1994, 12, 12), bookFive.getLaunchDate());
        assertEquals("Agile and Iterative Development: A Manager’s Guide", bookFive.getTitle());
        assertEquals(new BigDecimal("128.07"), bookFive.getPrice());
    }


    private void mockBook() {
        book.setAuthor("Martin Fowler");
        book.setTitle("Refactoring");
        book.setLaunchDate(LocalDate.of(2018, 11, 20));
        book.setPrice(new BigDecimal("89.90"));
    }
}

