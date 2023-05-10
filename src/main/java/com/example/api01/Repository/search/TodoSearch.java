package com.example.api01.Repository.search;

import com.example.api01.dto.PageRequestDTO;
import com.example.api01.dto.TodoDTO;
import org.springframework.data.domain.Page;

public interface TodoSearch {

    Page<TodoDTO> list(PageRequestDTO pageRequestDTO);

}
