package com.example.api01.Repository;

import com.example.api01.Repository.search.TodoSearch;
import com.example.api01.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {

}
