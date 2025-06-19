package br.com.aliriorios.done_and_dusted.web.dto.pageable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PageableDto {
    private List content = new ArrayList<>();
    private boolean first;
    private boolean last;

    @JsonProperty("page")
    private int number;
    private int size;

    @JsonProperty("PageElements")
    private int numberOfElements;
    private int totalPages;
    private int totalElements;
}
