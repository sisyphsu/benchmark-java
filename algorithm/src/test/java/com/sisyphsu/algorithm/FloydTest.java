package com.sisyphsu.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Floyd最短路径算法
 *
 * @author sulin
 * @since 2019-05-20 17:14:34
 */
@Slf4j
public class FloydTest {
    // 路径
    private static Floyd.Path[] lines = {
            Floyd.Path.valueOf('A', 'B', 12),
            Floyd.Path.valueOf('A', 'F', 16),
            Floyd.Path.valueOf('A', 'G', 14),

            Floyd.Path.valueOf('B', 'A', 12),
            Floyd.Path.valueOf('B', 'C', 10),
            Floyd.Path.valueOf('B', 'F', 7),

            Floyd.Path.valueOf('C', 'B', 10),
            Floyd.Path.valueOf('C', 'D', 3),
            Floyd.Path.valueOf('C', 'E', 5),
            Floyd.Path.valueOf('C', 'F', 6),

            Floyd.Path.valueOf('D', 'C', 3),
            Floyd.Path.valueOf('D', 'E', 4),

            Floyd.Path.valueOf('E', 'C', 5),
            Floyd.Path.valueOf('E', 'D', 4),
            Floyd.Path.valueOf('E', 'F', 2),
            Floyd.Path.valueOf('E', 'G', 8),

            Floyd.Path.valueOf('F', 'A', 16),
            Floyd.Path.valueOf('F', 'B', 7),
            Floyd.Path.valueOf('F', 'C', 6),
            Floyd.Path.valueOf('F', 'E', 2),
            Floyd.Path.valueOf('F', 'G', 9),

            Floyd.Path.valueOf('G', 'A', 14),
            Floyd.Path.valueOf('G', 'F', 9),
            Floyd.Path.valueOf('G', 'E', 8),
    };

    @Test
    public void test() {
        Floyd floyd = new Floyd(lines);
        for (Floyd.FinalPath path : floyd.listFinalPath()) {
            log.info("[{}->{}] {} \t {}", path.getStart(), path.getEnd(), path.getDist(), path.getWay());
        }
    }

}
