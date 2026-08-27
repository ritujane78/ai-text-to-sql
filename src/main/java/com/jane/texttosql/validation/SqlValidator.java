package com.jane.texttosql.validation;

import io.micrometer.core.instrument.config.validate.ValidationException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.stereotype.Service;

@Service
public class SqlValidator {
    public ValidationResult validateSql(String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if(!(statement instanceof Select)){
                return ValidationResult.failure("Only select statements are allowed. Unsafe SQL detected.");
            }
            return ValidationResult.success();

        } catch(JSQLParserException exception){
            ValidationResult.failure("Invalid SQL statement.");

        }
        return ValidationResult.success();
    }
    public void validateOrThrow(String sql) {
        ValidationResult result = validateSql(sql);
        if(!result.isValid()){
            throw new SqlValidationException(result.getMessage());
        }
    }
}
