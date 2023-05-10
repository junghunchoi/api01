package com.example.api01.repository;

import com.example.api01.Repository.TodoRepository;
import com.example.api01.dto.PageRequestDTO;
import com.example.api01.dto.PageResponseDTO;
import com.example.api01.dto.TodoDTO;
import com.example.api01.entity.Todo;
import java.time.LocalDate;
import java.util.stream.IntStream;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.asm.Advice.Local;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

@SpringBootTest
@Log4j2
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    public void testInsert() {

        IntStream.rangeClosed(1, 100).forEach(i -> {

            Todo todo = Todo.builder()
                            .title("Title" + i)
                            .dueDate(LocalDate.of(2022, (i % 12) + 1, (i % 30) + 1))
                            .wirter("user" + i)
                            .complete(false)
                            .build();

            todoRepository.save(todo);
        });
    }

    @Test
    public void selectTest() {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                                                      .from(LocalDate.of(2022, 10, 01))
                                                      .to(LocalDate.of(2023, 05, 30))
                                                      .build();

        Page<TodoDTO> result = todoRepository.list(pageRequestDTO);

        result.forEach(todoDTO -> log.info(todoDTO));

    }

}
