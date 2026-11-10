package com.marcelloabritta.restapi.controllers;

import com.marcelloabritta.restapi.controllers.docs.PersonControllerDocs;
import com.marcelloabritta.restapi.data.dto.v1.PersonDTO;
import com.marcelloabritta.restapi.services.PersonServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
@RequestMapping("api/person/v1")
@Tag(name = "People", description = "Endpoints for Managing people")
public class PersonController implements PersonControllerDocs {

    @Autowired
    private PersonServices service;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Override
    public List<PersonDTO> findAll() {

        return service.findAll();
    }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @Override
    public PersonDTO findById(@PathVariable("id") Long id) {

        return service.findById(id);
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @Override
    public PersonDTO create(@RequestBody PersonDTO person) {

        return service.create(person);
    }


    @PutMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @Override
    public PersonDTO update(@RequestBody PersonDTO person) {

        return service.update(person);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
