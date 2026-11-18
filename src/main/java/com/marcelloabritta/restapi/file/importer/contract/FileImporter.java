package com.marcelloabritta.restapi.file.importer.contract;

import com.marcelloabritta.restapi.data.dto.v1.PersonDTO;

import java.io.InputStream;
import java.util.List;

public interface FileImporter {

    List<PersonDTO> importFile(InputStream inputStream) throws Exception;
}
