package com.github.sisyphsu.benchmark.pojo;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

@Data
public class Egg {

    private boolean b = true;
    private long timstamp = System.currentTimeMillis();
    private String desc = RandomStringUtils.randomAlphanumeric(10);
    private Date date = new Date();

}