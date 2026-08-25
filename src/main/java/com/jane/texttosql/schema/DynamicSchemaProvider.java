package com.jane.texttosql.schema;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DynamicSchemaProvider implements SchemaProvider {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TableSchema> getTables() {
        Map<String, List<ColumnSchema>> columnsByTable = loadColumns();

        String sql = """
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = DATABASE()
              AND table_type = 'BASE TABLE'
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String tableName = rs.getString("table_name");

            return new TableSchema(
                    tableName,
                    null,
                    columnsByTable.getOrDefault(tableName, List.of())
            );
        });
    }

    private Map<String, List<ColumnSchema>> loadColumns() {
        String sql = """
            SELECT table_name, column_name
            FROM information_schema.columns
            WHERE table_schema = DATABASE()
            ORDER BY table_name, ordinal_position
            """;

        Map<String, List<ColumnSchema>> result = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            result
                    .computeIfAbsent(
                            rs.getString("table_name"),
                            t -> new ArrayList<>()
                    )
                    .add(
                            new ColumnSchema(
                                    rs.getString("column_name"),
                                    null
                            )
                    );
        });

        return result;
    }

    @Override
    public List<Relationship> getRelationships() {
        String sql = """
            SELECT
                kcu.table_name AS source_table,
                kcu.column_name AS source_column,
                kcu.referenced_table_name AS target_table,
                kcu.referenced_column_name AS target_column
            FROM information_schema.key_column_usage kcu
            WHERE kcu.table_schema = DATABASE()
              AND kcu.referenced_table_name IS NOT NULL
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new Relationship(
                                rs.getString("source_table"),
                                rs.getString("source_column"),
                                rs.getString("target_table"),
                                rs.getString("target_column")
                        )
        );
    }

    @Override
    public List<BusinessRule> getBusinessRules() {
        return List.of(
                new BusinessRule(
                        "Unless explicitly requested, consider only employees with status = 'ACTIVE'."
                ),
                new BusinessRule(
                        "A department is considered inactive if it has no ACTIVE employees."
                ),
                new BusinessRule(
                        "Do not assume data that is not present in the schema."
                )
        );
    }
}