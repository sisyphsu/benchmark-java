package com.github.sisyphsu.benchmark.json;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sulin
 * @since 2019-10-22 11:10:22
 */
public class LoopTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void test() throws Exception {
        Book book = new Book();
        Person person = new Person();

        book.person = person;
        person.books.add(book);

//        String json = MAPPER.writeValueAsString(person);
//        System.out.println(json);

        System.out.println(JSON.toJSONString(person));
    }

    @Data
    public static class Person {
        private int        id;
        private List<Book> books = new ArrayList<>();
    }

    @Data
    public static class Book {
        private int    id;
        private Person person;
    }

}
