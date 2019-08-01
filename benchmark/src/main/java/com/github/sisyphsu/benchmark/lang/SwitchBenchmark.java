package com.github.sisyphsu.benchmark.lang;

import com.github.sisyphsu.benchmark.Runner;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * 测试伪switch与ifelse的性能
 * <p>
 * ArrayList:
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * SwitchBenchmark.createByIfElse  avgt    9   4.434 ± 0.043  ns/op
 * SwitchBenchmark.createBySwitch  avgt    9  20.139 ± 0.095  ns/op
 * <p>
 * ArrayBlockingQueue:
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * SwitchBenchmark.createByIfElse  avgt    9  14.308 ± 0.157  ns/op
 * SwitchBenchmark.createBySwitch  avgt    9  29.148 ± 0.334  ns/op
 * <p>
 * ConcurrentLinkedQueue:
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * SwitchBenchmark.createByIfElse  avgt    9  11.441 ± 0.089  ns/op
 * SwitchBenchmark.createBySwitch  avgt    9  29.693 ± 0.240  ns/op
 * <p>
 * 通过Map和lambda实现的switch性能太差
 * <p>
 * 测试get与computeIfAbsent，发现两者存在性能差距：
 * Benchmark                    Mode  Cnt   Score   Error  Units
 * SwitchBenchmark.getCreator   avgt    9  17.407 ± 0.102  ns/op
 * SwitchBenchmark.getCreator2  avgt    9   2.499 ± 0.197  ns/op
 * <p>
 * 换用HashMap后性能变化：
 * Benchmark                    Mode  Cnt  Score   Error  Units
 * SwitchBenchmark.getCreator   avgt    9  3.560 ± 0.016  ns/op
 * SwitchBenchmark.getCreator2  avgt    9  2.197 ± 0.065  ns/op
 * 看起来是ConcurrentHashMap对computeIfAbsent的实现有问题所致，但整体性能基本都在2ns的Map#get耗时上。
 * <p>
 * <p>
 * ArrayList：
 * Benchmark                       Mode  Cnt  Score   Error  Units
 * SwitchBenchmark.createByIfElse  avgt    9  4.426 ± 0.040  ns/op
 * SwitchBenchmark.createBySwitch  avgt    9  4.458 ± 0.022  ns/op
 * <p>
 * ArrayBlockingQueue：
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * SwitchBenchmark.createByIfElse  avgt    9  14.243 ± 0.101  ns/op
 * SwitchBenchmark.createBySwitch  avgt    9  14.054 ± 0.378  ns/op
 * <p>
 * ConcurrentLinkedQueue：
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * SwitchBenchmark.createByIfElse  avgt    9  11.396 ± 0.099  ns/op
 * SwitchBenchmark.createBySwitch  avgt    9   9.090 ± 0.041  ns/op
 * <p>
 * 看起来伪switch确实能够提高一点点性能
 *
 * @author sulin
 * @since 2019-07-24 12:30:41
 */
@SuppressWarnings("all")
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SwitchBenchmark {

    private static final Class<? extends Collection> collType = ConcurrentLinkedQueue.class;
    private static final Class<?> itemType = Long.class;
    private static final int size = 10;

    /**
     * Create collection for benchmark
     */
    protected static <T extends Collection> T createByIfElse(Class<T> clz, Class itemType, int size) {
        Collection result = null;
        if (clz.isAssignableFrom(ArrayList.class)) {
            result = new ArrayList(size);
        } else if (clz.isAssignableFrom(LinkedList.class)) {
            result = new LinkedList();
        } else if (clz.isAssignableFrom(Stack.class)) {
            result = new Stack();
        } else if (clz.isAssignableFrom(Vector.class)) {
            result = new Vector(size);
        } else if (clz.isAssignableFrom(CopyOnWriteArrayList.class)) {
            result = new CopyOnWriteArrayList();
        } else if (clz.isAssignableFrom(HashSet.class)) {
            result = new HashSet(size);
        } else if (clz.isAssignableFrom(TreeSet.class)) {
            result = new TreeSet();
        } else if (clz.isAssignableFrom(LinkedHashSet.class)) {
            result = new LinkedHashSet(size);
        } else if (clz.isAssignableFrom(EnumSet.class)) {
            result = EnumSet.noneOf(itemType);
        } else if (clz.isAssignableFrom(CopyOnWriteArraySet.class)) {
            result = new CopyOnWriteArraySet();
        } else if (clz.isAssignableFrom(ConcurrentSkipListSet.class)) {
            result = new ConcurrentSkipListSet();
        } else if (clz.isAssignableFrom(ArrayBlockingQueue.class)) {
            result = new ArrayBlockingQueue(size);
        } else if (clz.isAssignableFrom(ArrayDeque.class)) {
            result = new ArrayDeque(size);
        } else if (clz.isAssignableFrom(DelayQueue.class)) {
            result = new DelayQueue();
        } else if (clz.isAssignableFrom(LinkedList.class)) {
            result = new LinkedList();
        } else if (clz.isAssignableFrom(LinkedBlockingDeque.class)) {
            result = new LinkedBlockingDeque(size);
        } else if (clz.isAssignableFrom(LinkedBlockingQueue.class)) {
            result = new LinkedBlockingQueue(size);
        } else if (clz.isAssignableFrom(LinkedTransferQueue.class)) {
            result = new LinkedTransferQueue();
        } else if (clz.isAssignableFrom(PriorityBlockingQueue.class)) {
            result = new PriorityBlockingQueue(size);
        } else if (clz.isAssignableFrom(PriorityQueue.class)) {
            result = new PriorityQueue(size);
        } else if (clz.isAssignableFrom(SynchronousQueue.class)) {
            result = new SynchronousQueue();
        } else if (clz.isAssignableFrom(ConcurrentLinkedDeque.class)) {
            result = new ConcurrentLinkedDeque();
        } else if (clz.isAssignableFrom(ConcurrentLinkedQueue.class)) {
            result = new ConcurrentLinkedQueue();
        }
        return (T) result;
    }

    private final static Creator NULL = (t, s) -> null;
    private final static Map<Class, Creator> CREATORS = new ConcurrentHashMap<>();
    private final static Map<Class, Creator> CREATOR_MAP = new ConcurrentHashMap<>();

    static {
        // List
        CREATORS.put(ArrayList.class, (t, size) -> new ArrayList<>(size));
        CREATORS.put(LinkedList.class, (t, size) -> new LinkedList<>());
        CREATORS.put(Vector.class, (t, size) -> new Vector<>(size));
        CREATORS.put(CopyOnWriteArrayList.class, (t, size) -> new CopyOnWriteArrayList<>());
        // Set
        CREATORS.put(HashSet.class, (t, size) -> new HashSet<>(size));
        CREATORS.put(TreeSet.class, (t, size) -> new TreeSet<>());
        CREATORS.put(LinkedHashSet.class, (t, size) -> new LinkedHashSet<>(size));
        CREATORS.put(EnumSet.class, (t, size) -> EnumSet.noneOf(t));
        CREATORS.put(CopyOnWriteArraySet.class, (t, size) -> new CopyOnWriteArraySet<>());
        CREATORS.put(ConcurrentSkipListSet.class, (t, size) -> new ConcurrentSkipListSet<>());
        // Queue
        CREATORS.put(ArrayBlockingQueue.class, (t, size) -> new ArrayBlockingQueue<>(size));
        CREATORS.put(ArrayDeque.class, (t, size) -> new ArrayDeque<>(size));
        CREATORS.put(DelayQueue.class, (t, size) -> new DelayQueue<>());
        CREATORS.put(LinkedBlockingDeque.class, (t, size) -> new LinkedBlockingDeque<>(size));
        CREATORS.put(LinkedBlockingQueue.class, (t, size) -> new LinkedBlockingQueue<>(size));
        CREATORS.put(LinkedTransferQueue.class, (t, size) -> new LinkedTransferQueue<>());
        CREATORS.put(PriorityBlockingQueue.class, (t, size) -> new PriorityBlockingQueue<>(size));
        CREATORS.put(PriorityQueue.class, (t, size) -> new PriorityQueue<>(size));
        CREATORS.put(SynchronousQueue.class, (t, size) -> new SynchronousQueue<>());
        CREATORS.put(ConcurrentLinkedDeque.class, (t, size) -> new ConcurrentLinkedDeque<>());
        CREATORS.put(ConcurrentLinkedQueue.class, (t, size) -> new ConcurrentLinkedQueue<>());
    }

    /**
     * Create an collection instance by the specified Type.
     */
    protected static <T extends Collection> T createBySwitch(Class<T> clz, Class<?> itemType, int size) {
        Creator creator = CREATOR_MAP.get(collType);
        if (creator == null) {
            creator = NULL;
            for (Map.Entry<Class, Creator> entry : CREATORS.entrySet()) {
                if (collType.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                }
            }
            CREATOR_MAP.put(collType, creator);
        }
        if (creator == NULL) {
            throw new IllegalArgumentException("Unsupported Type: " + collType);
        }
        return (T) creator.create(itemType, size);
    }

    @FunctionalInterface
    private interface Creator {
        Collection<?> create(Class itemType, int size);
    }

    @Benchmark
    public void createBySwitch() {
        createBySwitch(collType, itemType, size);
    }

    @Benchmark
    public void createByIfElse() {
        createByIfElse(collType, itemType, size);
    }

    //    @Benchmark
    public void getCreator() {
        Creator creator = CREATOR_MAP.computeIfAbsent(collType, t -> {
            for (Map.Entry<Class, Creator> entry : CREATORS.entrySet()) {
                if (collType.isAssignableFrom(entry.getKey())) {
                    return entry.getValue();
                }
            }
            return NULL;
        });
        if (creator == NULL) {
            throw new IllegalArgumentException("Unsupported Type: " + collType);
        }
    }

    //    @Benchmark
    public void getCreator2() {
        Creator creator = CREATOR_MAP.get(collType);
        if (creator == null) {
            creator = NULL;
            for (Map.Entry<Class, Creator> entry : CREATORS.entrySet()) {
                if (collType.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                }
            }
            CREATOR_MAP.put(collType, creator);
        }
        if (creator == NULL) {
            throw new IllegalArgumentException("Unsupported Type: " + collType);
        }
    }

    public static void main(String[] args) {
        createBySwitch(collType, itemType, size);
        createByIfElse(collType, itemType, size);
        Runner.run(SwitchBenchmark.class);
    }

}