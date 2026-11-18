package com.marcelloabritta.restapi.file.importer.factory;

import com.marcelloabritta.restapi.exception.BadRequestException;
import com.marcelloabritta.restapi.file.importer.contract.FileImporter;
import com.marcelloabritta.restapi.file.importer.impl.CsvImporter;
import com.marcelloabritta.restapi.file.importer.impl.XlsxImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileImporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileImporterFactory.class);

    private final CsvImporter csvImporter;
    private final XlsxImporter xlsxImporter;

    public FileImporterFactory(CsvImporter csvImporter, XlsxImporter xlsxImporter) {
        this.csvImporter = csvImporter;
        this.xlsxImporter = xlsxImporter;
    }

    public FileImporter getImporter(String fileName) throws Exception {
        if (fileName.endsWith(".xlsx")) {
            return xlsxImporter;
        } else if (fileName.endsWith(".csv")) {
            return csvImporter;
        } else {
            throw new BadRequestException("Invalid File Format.");
        }

    }
}
