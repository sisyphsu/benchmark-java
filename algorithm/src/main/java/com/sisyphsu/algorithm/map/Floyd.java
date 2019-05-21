package com.sisyphsu.algorithm.map;

import com.sisyphsu.algorithm.map.pojo.FinalPath;
import com.sisyphsu.algorithm.map.pojo.Path;

import java.util.*;

/**
 * Floyd图最短路径搜索算法
 *
 * @author sulin
 * @since 2019-05-20 17:29:05
 */
public class Floyd {

    private List<Character> vertexs = new ArrayList<>();
    private Integer[][] routers;
    private Integer[][] dists;

    public Floyd(Path[] paths) {
        // 汇总vertex
        Set<Character> keys = new TreeSet<>();
        for (Path line : paths) {
            keys.add(line.start);
            keys.add(line.end);
        }
        vertexs.addAll(keys);
        Map<Character, Integer> vertexMap = new HashMap<>();
        for (Character vertex : keys) {
            vertexMap.put(vertex, vertexMap.size());
        }
        // 汇总路径表
        routers = new Integer[keys.size()][keys.size()];
        dists = new Integer[keys.size()][keys.size()];
        for (Path line : paths) {
            int i = vertexMap.get(line.start);
            int j = vertexMap.get(line.end);
            dists[i][j] = line.distance;
            routers[i][j] = j;
        }
        for (int i = 0; i < keys.size(); i++) {
            dists[i][i] = 0;
        }
        // 计算最短路径
        for (int k = 0; k < dists.length; k++) {
            for (int i = 0; i < dists.length; i++) {
                for (int j = 0; j < dists.length; j++) {
                    if (dists[i][k] == null || dists[k][j] == null) {
                        continue; // 路径不通
                    }
                    if (dists[i][j] != null && dists[i][j] <= (dists[i][k] + dists[k][j])) {
                        continue; // 不需中转
                    }
                    dists[i][j] = dists[i][k] + dists[k][j];
                    routers[i][j] = k; // 记录i->j的最短路径需要绕道k
                }
            }
        }
    }

    // 归类最终结果
    public List<FinalPath> listFinalPath() {
        List<FinalPath> result = new ArrayList<>();
        for (int i = 0; i < routers.length; i++) {
            for (int j = 0; j < routers.length; j++) {
                if (i == j) {
                    continue;
                }
                char start = vertexs.get(i);
                char end = vertexs.get(j);
                Integer dist = dists[i][j];
                List<Character> way = this.findPath(i, j);
                if (way == null) {
                    continue;
                }
                way.add(0, start);
                result.add(new FinalPath(start, end, dist, way));
            }
        }
        return result;
    }

    public List<Character> findPath(int i, int j) {
        Integer k = routers[i][j];
        if (k == null) {
            return null; // 不可达
        }
        if (i == j) {
            return null; // 忽略自己
        }
        List<Character> result = new ArrayList<>();
        if (j == k) {
            result.add(vertexs.get(j));
            return result; // 直达
        }
        // 填充子节点
        List<Character> left = this.findPath(i, k);
        if (left != null) {
            result.addAll(left);
        }
        List<Character> right = this.findPath(k, j);
        if (right != null) {
            result.addAll(right);
        }
        return result;
    }

}
