package com.jane.texttosql.validation;

import com.jane.texttosql.schema.SchemaProvider;
import io.micrometer.core.instrument.config.validate.ValidationException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SqlValidator {
    private final double MAX_ALLOWED_LIMIT = 100;
    private final SchemaProvider schemaProvider;

    public SqlValidator(@Qualifier("cachedSchemaProvider") SchemaProvider schemaProvider) {
        this.schemaProvider = schemaProvider;
    }

    public ValidationResult validateSql(String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if(!(statement instanceof Select)){
                return ValidationResult.failure("Only select statements are allowed. Unsafe SQL detected.");
            }

            Select select = (Select) statement;
            Set<String> tables = SqlAstUtils.extractTables(select);
            System.out.println("tables = "+ tables);
            ValidationResult tableCheck = validateTables(tables);
            if(!tableCheck.isValid()) return tableCheck;

            Set<String> columns = SqlAstUtils.extractColumns(select);
            ValidationResult columnCheck = validateColumns(tables, columns);
            if(!columnCheck.isValid()) return columnCheck;

            ValidationResult limitCheck = validateLimit(select);
            if(!limitCheck.isValid()) return limitCheck;

            return ValidationResult.success();

        } catch(JSQLParserException exception){
            ValidationResult.failure("Invalid SQL statement.");

        }
        return ValidationResult.success();
    }
    private ValidationResult validateTables(Set<String> tables) {
        Set<String> allowedTables = schemaProvider.getTables().stream()
                .map(t -> t.getTableName().toLowerCase())
                .collect(Collectors.toSet());
        System.out.println("allowed tables = " + allowedTables);

        for(String table: tables){
            if(!allowedTables.contains(table)){
                return ValidationResult.failure("Query reference unknown or unauthorized table: " + table);
            }
        }
        return ValidationResult.success();

    }
    private ValidationResult validateColumns(Set<String> tables, Set<String> columns) {
        Map<String, Set<String>> tableColumns = schemaProvider.getTables()
                .stream()
                .collect(Collectors.toMap(
                        t -> t.getTableName().toLowerCase(),
                        t->t.getColumns()
                                .stream()
                                .map(c -> c.getName().toLowerCase())
                                .collect(Collectors.toSet())
                ));
        for(String column: columns){
            boolean found = false;
            for(String table: tables){
                Set<String> allowedColumns = tableColumns.get(table.toLowerCase());
                if(allowedColumns != null && allowedColumns.contains(column.toLowerCase())){
                    found = true;
                    break;
                }
            }
            if(!found){
                return ValidationResult.failure("Column reference unknown or unauthorized column: " + column);
            }
        }
        return ValidationResult.success();
    }
    private ValidationResult validateLimit(Select select) {
        Long limit = SqlAstUtils.extractLimit(select);
        if(limit == null) return ValidationResult.failure("No limit specified. Maximum allowed limit: " + MAX_ALLOWED_LIMIT);

        if (limit > MAX_ALLOWED_LIMIT) {
            return ValidationResult.failure("Limit value exceeds maximum allowed limit of " + MAX_ALLOWED_LIMIT );
        }
        return ValidationResult.success();
    }
    public void validateOrThrow(String sql) {
        ValidationResult result = validateSql(sql);
        if(!result.isValid()){
            throw new SqlValidationException(result.getMessage() + ". SQL: " +sql);
        }
    }
}
