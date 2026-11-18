package com.marcelloabritta.restapi.file.exporter.contract;

import com.marcelloabritta.restapi.data.dto.v1.PersonDTO;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.List;

public interface FileExporter {

    Resource exportFile(List<PersonDTO> people) throws Exception;

}
