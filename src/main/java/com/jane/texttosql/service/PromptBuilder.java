package com.jane.texttosql.service;

import com.jane.texttosql.schema.Relationship;
import com.jane.texttosql.schema.SchemaProvider;
import com.jane.texttosql.schema.TableSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromptBuilder {

    private final SchemaProvider schemaProvider;

    public String buildPrompt(String question){

        String tables = schemaProvider.getTables().stream()
                .map(this::renderTable)
                .collect(Collectors.joining("\n"));

        String relationships = schemaProvider.getRelationships().stream()
                .map(this::renderRelationship)
                .collect(Collectors.joining("\n"));
        return """
               You are an assistant that generates SQL queries for a MySQL database. 

               Database schema:
               %s

               Relationship:
               %s
               
                Instructions:
                   - Return ONLY the SQL query.
                   - Do NOT include explanations.
                   - Do NOT include markdown.
                   - Do NOT include code fences.
                   - The output must be a single SQL statement.

               Generate a SQL query for the following question:
               %s
               """.formatted(tables, relationships, question);
    }
    private String renderTable(TableSchema table){
        String columns = String.join(", ", table.getColumns());
        return "- %s(%s)".formatted(table.getTableName(), columns);
    }

    private String renderRelationship(Relationship relationship){
        return "%s.%s references %s.%s".formatted(
                relationship.getFromTable(),
                relationship.getFromColumn(),
                relationship.getToTable(),
                relationship.getToColumn()
        );
    }
}
