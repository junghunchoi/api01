package com.example.api01.repository;

import com.example.api01.Repository.TodoRepository;
import com.example.api01.entity.Todo;
import java.time.LocalDate;
import java.util.stream.IntStream;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
