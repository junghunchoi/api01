package com.example.api01.Repository.search;


import com.example.api01.dto.PageRequestDTO;
import com.example.api01.dto.TodoDTO;
import com.example.api01.entity.QTodo;
import com.example.api01.entity.Todo;
import com.example.api01.service.TodoServiceImpl;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch{

    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<TodoDTO> list(PageRequestDTO pageRequestDTO) {

        QTodo todo = QTodo.todo;

        JPQLQuery<Todo> query = from(todo);

        if (pageRequestDTO.getFrom() != null && pageRequestDTO.getTo() != null) {

            BooleanBuilder fromToBuilder = new BooleanBuilder();

            fromToBuilder.and(todo.dueDate.goe(pageRequestDTO.getFrom()));
            fromToBuilder.and(todo.dueDate.loe(pageRequestDTO.getTo()));

            query.where(fromToBuilder);
        }

        if (pageRequestDTO.getComplete() != null) {
            query.where(todo.complete.eq(pageRequestDTO.getComplete()));
        }

        this.getQuerydsl().applyPagination(pageRequestDTO.getPageable("tno"), query);

        JPQLQuery<TodoDTO> dtoQuery = query.select(
            Projections.bean(TodoDTO.class, todo.tno, todo.title, todo.dueDate, todo.complete,
                todo.wirter));

        List<TodoDTO> list = dtoQuery.fetch();

        long count = dtoQuery.fetchCount();


        return new PageImpl<>(list,pageRequestDTO.getPageable("tno"),count);
    }
}
