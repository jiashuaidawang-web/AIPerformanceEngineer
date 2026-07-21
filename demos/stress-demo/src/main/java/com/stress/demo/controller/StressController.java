package com.stress.demo.controller;

import com.stress.demo.entity.Product;
import com.stress.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StressController {

    private final ProductService productService;

    @Value("${spring.datasource.mysql.jdbc-url:}")
    private String dbUrl;

    private static final List<byte[]> MEMORY_LEAK = new ArrayList<byte[]>();
    private static final Map<String, Object> CACHE = new ConcurrentHashMap<String, Object>();

    private static Map<String, Object> m(Object... kvs) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < kvs.length; i += 2) {
            map.put((String) kvs[i], kvs[i + 1]);
        }
        return map;
    }

    @GetMapping("/products")
    public Object listProducts(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "100") int size) {
        long start = System.currentTimeMillis();
        List<Product> products = productService.findAll(page, Math.min(size, 500));
        return m("data", products, "page", page, "size", products.size(),
                "elapsed_ms", System.currentTimeMillis() - start);
    }

    @GetMapping("/products/{id}")
    public Object getProduct(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        Product p = productService.findById(id);
        return m("data", p, "elapsed_ms", System.currentTimeMillis() - start);
    }

    @GetMapping("/products/search")
    public Object search(@RequestParam(defaultValue = "a") String keyword) {
        long start = System.currentTimeMillis();
        List<Product> results = productService.search(keyword);
        return m("data", results, "keyword", keyword, "hits", results.size(),
                "elapsed_ms", System.currentTimeMillis() - start);
    }

    @PostMapping("/products")
    public Object createProduct(@RequestBody Product product) {
        long start = System.currentTimeMillis();
        if (product.getCreatedAt() == null) product.setCreatedAt(LocalDateTime.now());
        Product saved = productService.create(product);
        return m("data", saved, "elapsed_ms", System.currentTimeMillis() - start);
    }

    @GetMapping("/compute/fibonacci/{n}")
    public Object fibonacci(@PathVariable int n) {
        long start = System.currentTimeMillis();
        long result = fib(Math.min(n, 45));
        return m("result", result, "n", n, "elapsed_ms", System.currentTimeMillis() - start);
    }

    private long fib(int n) {
        if (n <= 1) return n;
        return fib(n - 1) + fib(n - 2);
    }

    @GetMapping("/compute/prime/{n}")
    public Object prime(@PathVariable int n) {
        long start = System.currentTimeMillis();
        long result = findNthPrime(Math.min(n, 100000));
        return m("result", result, "n", n, "elapsed_ms", System.currentTimeMillis() - start);
    }

    private long findNthPrime(int n) {
        int count = 0;
        long num = 2;
        while (count < n) {
            if (isPrime(num)) count++;
            num++;
        }
        return num - 1;
    }

    private boolean isPrime(long num) {
        if (num < 2) return false;
        for (long i = 2; i * i <= num; i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    @GetMapping("/slow")
    public Object slow(@RequestParam(defaultValue = "1000") long delayMs) {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(Math.min(delayMs, 30000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return m("delayed_ms", delayMs, "elapsed_ms", System.currentTimeMillis() - start);
    }

    @GetMapping("/memory/leak")
    public Object leakMemory(@RequestParam(defaultValue = "10") int mb) {
        long start = System.currentTimeMillis();
        byte[] leaked = new byte[mb * 1024 * 1024];
        Arrays.fill(leaked, (byte) 0xAA);
        MEMORY_LEAK.add(leaked);
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        return m("leaked_mb", mb, "total_leaked_objects", MEMORY_LEAK.size(),
                "heap_used_mb", heap.getUsed() / 1024 / 1024,
                "heap_max_mb", heap.getMax() / 1024 / 1024,
                "elapsed_ms", System.currentTimeMillis() - start);
    }

    @PostMapping("/memory/clear")
    public Object clearLeak() {
        MEMORY_LEAK.clear();
        System.gc();
        return m("cleared", true, "remaining", MEMORY_LEAK.size());
    }

    @GetMapping("/cache/{key}")
    public Object cache(@PathVariable String key) {
        long start = System.currentTimeMillis();
        boolean hit = CACHE.containsKey(key);
        if (!hit) {
            CACHE.put(key, "value-" + UUID.randomUUID());
        }
        return m("key", key, "hit", hit, "cache_size", CACHE.size(),
                "elapsed_ms", System.currentTimeMillis() - start);
    }

    @GetMapping("/stats")
    public Object stats() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("heap_used_mb", heap.getUsed() / 1024 / 1024);
        result.put("heap_max_mb", heap.getMax() / 1024 / 1024);
        result.put("heap_usage_percent", Math.round((double) heap.getUsed() / heap.getMax() * 100));
        result.put("active_threads", Thread.activeCount());
        result.put("db_url", dbUrl);
        result.put("leaked_objects", MEMORY_LEAK.size());
        result.put("cache_entries", CACHE.size());
        result.put("timestamp", LocalDateTime.now());
        return result;
    }
}
