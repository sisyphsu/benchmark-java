package com.sisyphsu.algorithm.map.pojo;

import java.util.List;

/**
 * 最短路径
 */
public class FinalPath {

    public final char start;
    public final char end;
    public final Integer dist;
    public final List<Character> way;

    public FinalPath(char start, char end, Integer dist, List<Character> way) {
        this.start = start;
        this.end = end;
        this.dist = dist;
        this.way = way;
    }
}
