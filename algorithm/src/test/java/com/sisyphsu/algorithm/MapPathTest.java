package com.sisyphsu.algorithm;

import com.sisyphsu.algorithm.map.Floyd;
import com.sisyphsu.algorithm.map.pojo.FinalPath;
import com.sisyphsu.algorithm.map.pojo.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Floyd最短路径算法
 *
 * @author sulin
 * @since 2019-05-20 17:14:34
 */
@Slf4j
public class MapPathTest {
    // 路径
    private static Path[] lines = {
            Path.valueOf('A', 'B', 12),
            Path.valueOf('A', 'F', 16),
            Path.valueOf('A', 'G', 14),

            Path.valueOf('B', 'A', 12),
            Path.valueOf('B', 'C', 10),
            Path.valueOf('B', 'F', 7),

            Path.valueOf('C', 'B', 10),
            Path.valueOf('C', 'D', 3),
            Path.valueOf('C', 'E', 5),
            Path.valueOf('C', 'F', 6),

            Path.valueOf('D', 'C', 3),
            Path.valueOf('D', 'E', 4),

            Path.valueOf('E', 'C', 5),
            Path.valueOf('E', 'D', 4),
            Path.valueOf('E', 'F', 2),
            Path.valueOf('E', 'G', 8),

            Path.valueOf('F', 'A', 16),
            Path.valueOf('F', 'B', 7),
            Path.valueOf('F', 'C', 6),
            Path.valueOf('F', 'E', 2),
            Path.valueOf('F', 'G', 9),

            Path.valueOf('G', 'A', 14),
            Path.valueOf('G', 'F', 9),
            Path.valueOf('G', 'E', 8),
    };

    @Test
    public void test() {
        Floyd floyd = new Floyd(lines);
        for (FinalPath path : floyd.listFinalPath()) {
            log.info("[{}->{}] {} \t {}", path.start, path.end, path.dist, path.way);
        }
    }

}
