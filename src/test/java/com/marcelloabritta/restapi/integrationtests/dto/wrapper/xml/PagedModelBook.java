package com.marcelloabritta.restapi.integrationtests.dto.wrapper.xml;

import com.marcelloabritta.restapi.integrationtests.dto.BookDTO;
import jakarta.xml.bind.annotation.XmlElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PagedModelBook implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "content")
    private List<BookDTO> content;

    public PagedModelBook() {
    }

    public List<BookDTO> getContent() {
        return content;
    }

    public void setContent(List<BookDTO> content) {
        this.content = content;
    }
}
