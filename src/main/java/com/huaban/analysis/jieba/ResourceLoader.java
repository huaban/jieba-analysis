package com.huaban.analysis.jieba;

import java.io.IOException;
import java.io.InputStream;

/**
 * Resource loader utility for both JVM and GraalVM Native Image environments.
 * This class provides a unified way to load resources that works correctly
 * in both traditional JVM and Native Image compiled applications.
 */
public class ResourceLoader {

    /**
     * Detect if running in GraalVM Native Image environment
     */
    private static final boolean IS_NATIVE_IMAGE = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    /**
     * Load a resource as an InputStream.
     * 
     * @param path the resource path (should start with /)
     * @return InputStream of the resource
     * @throws ResourceNotFoundException if resource is not found
     */
    public static InputStream loadResourceAsStream(String path) {
        // Try to load resource using class loader
        InputStream is = ResourceLoader.class.getResourceAsStream(path);

        if (is == null) {
            // Fallback: try without leading slash
            String altPath = path.startsWith("/") ? path.substring(1) : "/" + path;
            is = ResourceLoader.class.getResourceAsStream(altPath);
        }

        if (is == null) {
            throw new ResourceNotFoundException(
                    String.format("Resource not found: %s (Native Image: %s)", path, IS_NATIVE_IMAGE));
        }

        return is;
    }

    /**
     * Check if running in Native Image environment
     * 
     * @return true if running in Native Image, false otherwise
     */
    public static boolean isNativeImage() {
        return IS_NATIVE_IMAGE;
    }

    /**
     * Validate that a resource exists
     * 
     * @param path the resource path
     * @return true if resource exists, false otherwise
     */
    public static boolean resourceExists(String path) {
        try (InputStream is = ResourceLoader.class.getResourceAsStream(path)) {
            return is != null;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Custom exception for resource loading failures
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }

        public ResourceNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
