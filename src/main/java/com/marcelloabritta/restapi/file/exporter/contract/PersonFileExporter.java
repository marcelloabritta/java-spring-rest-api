package com.marcelloabritta.restapi.file.exporter.contract;

import com.marcelloabritta.restapi.data.dto.v1.PersonDTO;
import org.springframework.core.io.Resource;

public interface PersonFileExporter {
    Resource exportPerson(PersonDTO person) throws Exception;
}
