package com.kuncoro.todo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemProperties {
    private Integer code;
    private Map<String, String> errors;
}

