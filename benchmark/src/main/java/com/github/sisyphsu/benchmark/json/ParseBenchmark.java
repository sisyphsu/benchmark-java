package com.github.sisyphsu.benchmark.json;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sisyphsu.benchmark.Runner;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 测试JSON解析的性能指标
 * <p>
 * Benchmark                                                      Mode  Cnt      Score      Error   Units
 * ParseBenchmark.fastjson2Tree                                   avgt    9      8.511 ±    0.227   us/op
 * ParseBenchmark.fastjson2Tree:·gc.alloc.rate                    avgt    9   1477.133 ±   39.693  MB/sec
 * ParseBenchmark.fastjson2Tree:·gc.alloc.rate.norm               avgt    9  15392.001 ±    0.001    B/op
 * ParseBenchmark.fastjson2Tree:·gc.churn.PS_Eden_Space           avgt    9   1537.417 ±   90.663  MB/sec
 * ParseBenchmark.fastjson2Tree:·gc.churn.PS_Eden_Space.norm      avgt    9  16025.705 ± 1135.257    B/op
 * ParseBenchmark.fastjson2Tree:·gc.churn.PS_Survivor_Space       avgt    9      0.107 ±    0.046  MB/sec
 * ParseBenchmark.fastjson2Tree:·gc.churn.PS_Survivor_Space.norm  avgt    9      1.113 ±    0.463    B/op
 * ParseBenchmark.fastjson2Tree:·gc.count                         avgt    9    131.000             counts
 * ParseBenchmark.fastjson2Tree:·gc.time                          avgt    9    103.000                 ms
 * ParseBenchmark.fastjson2Tree:·mem.heap                         avgt    9    345.206                 MB
 * ParseBenchmark.fastjson2Tree:·mem.nonheap                      avgt    9     16.918                 MB
 * ParseBenchmark.gson2Tree                                       avgt    9      9.203 ±    0.235   us/op
 * ParseBenchmark.gson2Tree:·gc.alloc.rate                        avgt    9   1843.660 ±   46.626  MB/sec
 * ParseBenchmark.gson2Tree:·gc.alloc.rate.norm                   avgt    9  20768.001 ±    0.001    B/op
 * ParseBenchmark.gson2Tree:·gc.churn.PS_Eden_Space               avgt    9   1857.655 ±  155.115  MB/sec
 * ParseBenchmark.gson2Tree:·gc.churn.PS_Eden_Space.norm          avgt    9  20930.635 ± 1837.119    B/op
 * ParseBenchmark.gson2Tree:·gc.churn.PS_Survivor_Space           avgt    9      0.141 ±    0.089  MB/sec
 * ParseBenchmark.gson2Tree:·gc.churn.PS_Survivor_Space.norm      avgt    9      1.590 ±    1.036    B/op
 * ParseBenchmark.gson2Tree:·gc.count                             avgt    9    189.000             counts
 * ParseBenchmark.gson2Tree:·gc.time                              avgt    9    138.000                 ms
 * ParseBenchmark.gson2Tree:·mem.heap                             avgt    9    694.204                 MB
 * ParseBenchmark.gson2Tree:·mem.nonheap                          avgt    9     15.914                 MB
 * ParseBenchmark.jackson2Tree                                    avgt    9      7.966 ±    0.318   us/op
 * ParseBenchmark.jackson2Tree:·gc.alloc.rate                     avgt    9   1643.654 ±   65.488  MB/sec
 * ParseBenchmark.jackson2Tree:·gc.alloc.rate.norm                avgt    9  16024.001 ±    0.001    B/op
 * ParseBenchmark.jackson2Tree:·gc.churn.PS_Eden_Space            avgt    9   1668.602 ±  166.780  MB/sec
 * ParseBenchmark.jackson2Tree:·gc.churn.PS_Eden_Space.norm       avgt    9  16259.783 ± 1172.701    B/op
 * ParseBenchmark.jackson2Tree:·gc.churn.PS_Survivor_Space        avgt    9      0.214 ±    0.112  MB/sec
 * ParseBenchmark.jackson2Tree:·gc.churn.PS_Survivor_Space.norm   avgt    9      2.096 ±    1.144    B/op
 * ParseBenchmark.jackson2Tree:·gc.count                          avgt    9    274.000             counts
 * ParseBenchmark.jackson2Tree:·gc.time                           avgt    9    178.000                 ms
 * ParseBenchmark.jackson2Tree:·mem.heap                          avgt    9    204.829                 MB
 * ParseBenchmark.jackson2Tree:·mem.nonheap                       avgt    9     16.146                 MB
 *
 * @author sulin
 * @since 2019-05-08 15:22:06
 */
@Warmup(iterations = 1, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ParseBenchmark {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Gson gson = new Gson();

    private static final String json = "{\"medications\":[{\"aceInhibitors\":[{\"name\":\"lisinopril\",\"strength\":\"10 mg Tab\",\"dose\":\"1 tab\",\"route\":\"PO\",\"sig\":\"daily\",\"pillCount\":\"#90\",\"refills\":\"Refill 3\"}],\"antianginal\":[{\"name\":\"nitroglycerin\",\"strength\":\"0.4 mg Sublingual Tab\",\"dose\":\"1 tab\",\"route\":\"SL\",\"sig\":\"q15min PRN\",\"pillCount\":\"#30\",\"refills\":\"Refill 1\"}],\"anticoagulants\":[{\"name\":\"warfarin sodium\",\"strength\":\"3 mg Tab\",\"dose\":\"1 tab\",\"route\":\"PO\",\"sig\":\"daily\",\"pillCount\":\"#90\",\"refills\":\"Refill 3\"}],\"betaBlocker\":[{\"name\":\"metoprolol tartrate\",\"strength\":\"25 mg Tab\",\"dose\":\"1 tab\",\"route\":\"PO\",\"sig\":\"daily\",\"pillCount\":\"#90\",\"refills\":\"Refill 3\"}],\"diuretic\":[{\"name\":\"furosemide\",\"strength\":\"40 mg Tab\",\"dose\":\"1 tab\",\"route\":\"PO\",\"sig\":\"daily\",\"pillCount\":\"#90\",\"refills\":\"Refill 3\"}],\"mineral\":[{\"name\":\"potassium chloride ER\",\"strength\":\"10 mEq Tab\",\"dose\":\"1 tab\",\"route\":\"PO\",\"sig\":\"daily\",\"pillCount\":\"#90\",\"refills\":\"Refill 3\"}]}],\"labs\":[{\"name\":\"Arterial Blood Gas\",\"time\":\"Today\",\"location\":\"Main Hospital Lab\"},{\"name\":\"BMP\",\"time\":\"Today\",\"location\":\"Primary Care Clinic\"},{\"name\":\"BNP\",\"time\":\"3 Weeks\",\"location\":\"Primary Care Clinic\"},{\"name\":\"BUN\",\"time\":\"1 Year\",\"location\":\"Primary Care Clinic\"},{\"name\":\"Cardiac Enzymes\",\"time\":\"Today\",\"location\":\"Primary Care Clinic\"},{\"name\":\"CBC\",\"time\":\"1 Year\",\"location\":\"Primary Care Clinic\"},{\"name\":\"Creatinine\",\"time\":\"1 Year\",\"location\":\"Main Hospital Lab\"},{\"name\":\"Electrolyte Panel\",\"time\":\"1 Year\",\"location\":\"Primary Care Clinic\"},{\"name\":\"Glucose\",\"time\":\"1 Year\",\"location\":\"Main Hospital Lab\"},{\"name\":\"PT/INR\",\"time\":\"3 Weeks\",\"location\":\"Primary Care Clinic\"},{\"name\":\"PTT\",\"time\":\"3 Weeks\",\"location\":\"Coumadin Clinic\"},{\"name\":\"TSH\",\"time\":\"1 Year\",\"location\":\"Primary Care Clinic\"}],\"imaging\":[{\"name\":\"Chest X-Ray\",\"time\":\"Today\",\"location\":\"Main Hospital Radiology\"},{\"name\":\"Chest X-Ray\",\"time\":\"Today\",\"location\":\"Main Hospital Radiology\"},{\"name\":\"Chest X-Ray\",\"time\":\"Today\",\"location\":\"Main Hospital Radiology\"}]}";

    @Benchmark
    public void fastjson2Tree() {
        JSON.parse(json);
    }

    @Benchmark
    public void gson2Tree() {
        gson.fromJson(json, Map.class);
    }

    @Benchmark
    public void jackson2Tree() throws IOException {
        mapper.readTree(json);
    }

    public static void main(String[] args) throws Exception {
        Runner.run(ParseBenchmark.class);
    }

}
