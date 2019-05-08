package com.github.sisyphsu.benchmark.pojo;

import lombok.Data;

import java.util.*;

/**
 * @author sulin
 * @since 2019-05-08 10:57:59
 */
@Data
public class EggCake {

    private long id = System.currentTimeMillis();
    private Egg egg = new Egg();
    private List<String> tags = new ArrayList<>();
    private Map<String, Object> props = new HashMap<>();

    public EggCake() {
        tags.add("hello");
        tags.add("world");
        tags.add("gson");
        tags.add("fastjson");
        tags.add("jackson");

        props.put("seq", 1);
        props.put("color", "#FFFFFF");
        props.put("time", new Date());
    }

}
