package com.marcelloabritta.restapi.file.exporter.factory;

import com.marcelloabritta.restapi.exception.BadRequestException;
import com.marcelloabritta.restapi.file.exporter.MediaTypes;
import com.marcelloabritta.restapi.file.exporter.contract.FileExporter;
import com.marcelloabritta.restapi.file.exporter.contract.PersonFileExporter;
import com.marcelloabritta.restapi.file.exporter.impl.CsvExporter;
import com.marcelloabritta.restapi.file.exporter.impl.PdfExporter;
import com.marcelloabritta.restapi.file.exporter.impl.XlsxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileExporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);

    private final XlsxExporter xlsxExporter;
    private final CsvExporter csvExporter;
    private final PdfExporter pdfExporter;

    public FileExporterFactory(XlsxExporter xlsxExporter, CsvExporter csvExporter, PdfExporter pdfExporter) {
        this.xlsxExporter = xlsxExporter;
        this.csvExporter = csvExporter;
        this.pdfExporter = pdfExporter;
    }

    public FileExporter getExporter(String acceptHeader) throws Exception {
        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)) {
            return xlsxExporter;
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
            return csvExporter;
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_PDF_VALUE)) {
            return pdfExporter;
        } else {
            throw new BadRequestException("Invalid File Format.");
        }

    }

    public PersonFileExporter getPersonExporter(String acceptHeader) throws Exception {
        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_PDF_VALUE)) {
            return pdfExporter;
        } else {
            throw new BadRequestException("Only PDF format is supported for this operation.");
        }
    }
}
