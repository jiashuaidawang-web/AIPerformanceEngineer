package com.aipe.connector.linux.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * /proc 文件读取器
 *
 * <p>统一读取 Linux /proc 文件系统。
 */
public final class ProcFileReader {

    private static final Logger log = LoggerFactory.getLogger(ProcFileReader.class);
    private static final String DEFAULT_PROC_PATH = "/proc";

    private final String procPath;

    public ProcFileReader() {
        this(DEFAULT_PROC_PATH);
    }

    public ProcFileReader(String procPath) {
        this.procPath = procPath != null ? procPath : DEFAULT_PROC_PATH;
    }

    public String read(String relativePath) {
        Path path = Paths.get(procPath, relativePath);
        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Failed to read /proc/{}: {}", relativePath, e.getMessage());
            return "";
        }
    }

    public List<String> readLines(String relativePath) {
        Path path = Paths.get(procPath, relativePath);
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Failed to read /proc/{}: {}", relativePath, e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public String getProcPath() {
        return procPath;
    }
}
