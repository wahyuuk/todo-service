package com.kuncoro.todo.exception;

/**
 * Centralized Problem Type URIs for RFC 7807 responses.
 * Use URN-style identifiers to avoid external dependencies.
 * In production, consider hosting docs at these URIs or mapping to resolvable URLs.
 */
public final class ProblemTypes {
    private ProblemTypes() {}

    public static final String VALIDATION_ERROR = "urn:problem:todo:validation-error";
    public static final String NOT_FOUND = "urn:problem:common:not-found";
    public static final String INTERNAL_SERVER_ERROR = "urn:problem:todo:internal-server-error";
}
