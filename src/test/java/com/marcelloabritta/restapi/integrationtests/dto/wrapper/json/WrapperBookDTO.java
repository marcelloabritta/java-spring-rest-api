package com.marcelloabritta.restapi.integrationtests.dto.wrapper.json;

import com.marcelloabritta.restapi.integrationtests.dto.BookDTO;

import java.util.List;

public class WrapperBookDTO {
    private List<BookDTO> content;

    public WrapperBookDTO() {
    }

    public List<BookDTO> getContent() {
        return content;
    }

    public void setContent(List<BookDTO> content) {
        this.content = content;
    }
}
