package com.jane.texttosql.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SqlExecutionService {

    private final JdbcTemplate template;
    public List<Map<String, Object>> executeQuery(String query){
        return template.queryForList(query);
    }
}
