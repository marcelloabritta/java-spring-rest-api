package com.marcelloabritta.restapi.integrationtests.controllers.withxml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.marcelloabritta.restapi.config.TestConfigs;
import com.marcelloabritta.restapi.integrationtests.dto.AccountCredentialsDTO;
import com.marcelloabritta.restapi.integrationtests.dto.PersonDTO;
import com.marcelloabritta.restapi.integrationtests.dto.TokenDTO;
import com.marcelloabritta.restapi.integrationtests.dto.wrapper.json.WrapperPersonDTO;
import com.marcelloabritta.restapi.integrationtests.dto.wrapper.xml.PagedModelPerson;
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

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerXmlTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static XmlMapper xmlMapper;
    private static PersonDTO person;
    private static TokenDTO tokenDto;

    @BeforeAll
    static void setUp() {
        xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonDTO();
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

        tokenDto = xmlMapper.readValue(content, TokenDTO.class);

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_FRONT)
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDto.getAccessToken())
                .setBasePath("/api/v1/person")
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
        mockPerson();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .body(person)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonDTO createdPerson = xmlMapper.readValue(content, PersonDTO.class);
        person = createdPerson;


        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());
    }

    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        person.setLastName("Benedict Torvalds");

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .body(person)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonDTO createdPerson = xmlMapper.readValue(content, PersonDTO.class);
        person = createdPerson;


        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());
    }

    @Order(3)
    @Test
    void findByIdTest() throws JsonProcessingException {

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .pathParam("id", person.getId())
                .accept(MediaType.APPLICATION_XML_VALUE)
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonDTO createdPerson = xmlMapper.readValue(content, PersonDTO.class);
        person = createdPerson;

        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());
    }

    @Order(4)
    @Test
    void disableTest() throws JsonProcessingException {

        var content = given(specification)
                .pathParam("id", person.getId())
                .accept(MediaType.APPLICATION_XML_VALUE)
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonDTO createdPerson = xmlMapper.readValue(content, PersonDTO.class);
        person = createdPerson;

        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertFalse(createdPerson.getEnabled());
    }

    @Order(5)
    @Test
    void deleteTest() throws JsonProcessingException {

        given(specification)
                .pathParam("id", person.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);


    }

    @Order(6)
    @Test
    void findAllTest() throws JsonProcessingException {

        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .queryParams("page", 3, "size", 12, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PagedModelPerson wrapper = xmlMapper.readValue(content, PagedModelPerson.class);
        List<PersonDTO> people = wrapper.getContent();

        PersonDTO personOne = people.getFirst();
        person = personOne;

        assertTrue(personOne.getId() > 0);

        assertEquals("Allin", personOne.getFirstName());
        assertEquals("Emmot", personOne.getLastName());
        assertEquals("7913 Lindbergh Way", personOne.getAddress());
        assertEquals("Male", personOne.getGender());
        assertFalse(personOne.getEnabled());

        PersonDTO personFive = people.get(4);
        person = personFive;

        assertTrue(personFive.getId() > 0);

        assertEquals("Alonso", personFive.getFirstName());
        assertEquals("Luchelli", personFive.getLastName());
        assertEquals("9 Doe Crossing Avenue", personFive.getAddress());
        assertEquals("Male", personFive.getGender());
        assertFalse(personFive.getEnabled());
    }

    @Order(7)
    @Test
    void findByNameTest() throws JsonProcessingException {

        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .pathParam("firstName", "and")
                .queryParams("page", 0, "size", 12, "direction", "asc")
                .when()
                .get("findByName/{firstName}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PagedModelPerson wrapper = xmlMapper.readValue(content, PagedModelPerson.class);
        List<PersonDTO> people = wrapper.getContent();

        PersonDTO personOne = people.getFirst();

        assertTrue(personOne.getId() > 0);

        assertEquals("Alessandro", personOne.getFirstName());
        assertEquals("McFaul", personOne.getLastName());
        assertEquals("5 Lukken Plaza", personOne.getAddress());
        assertEquals("Male", personOne.getGender());
        assertTrue(personOne.getEnabled());

        PersonDTO personFive = people.get(4);

        assertTrue(personFive.getId() > 0);

        assertEquals("Brandyn", personFive.getFirstName());
        assertEquals("Grasha", personFive.getLastName());
        assertEquals("96 Mosinee Parkway", personFive.getAddress());
        assertEquals("Male", personFive.getGender());
        assertTrue(personFive.getEnabled());
    }


    private void mockPerson() {
        person.setFirstName("Linus");
        person.setLastName("Torvalds");
        person.setAddress("Helsinki - Finland");
        person.setGender("Male");
        person.setEnabled(true);
        person.setProfileUrl("https://www.linkedin.com/in/marcelloabritta");
        person.setPhotoUrl("https://www.linkedin.com/in/marcelloabritta");
    }
}