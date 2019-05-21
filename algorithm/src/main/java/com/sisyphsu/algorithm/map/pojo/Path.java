
package com.sisyphsu.algorithm.map.pojo;

/**
 * 单向路径
 */
public class Path {

    public char start;
    public char end;
    public int distance;

    public static Path valueOf(char start, char end, int dist) {
        Path path = new Path();
        path.start = start;
        path.end = end;
        path.distance = dist;
        return path;
    }

}