package com.jane.texttosql.service;

import com.jane.texttosql.dto.TextToSqlRequest;
import com.jane.texttosql.dto.TextToSqlResponse;
import com.jane.texttosql.validation.SqlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextToSqlService {

    private final PromptBuilder promptBuilder;

    private final SqlExecutionService sqlExecutionService;
    private final ChatClient chatClient;
    private final SqlValidator sqlValidator;

    public TextToSqlResponse handle(TextToSqlRequest request ){
        String prompt = promptBuilder.buildPrompt(request.getQuestion());

        log.info("Prompting text to sql request: {}", prompt);
        String generatedSql = chatClient.prompt(prompt).call().content();

        sqlValidator.validateOrThrow(generatedSql);

        generatedSql = normalizeSql(generatedSql);
        log.info("Generated SQL: {}", generatedSql);

        List<Map<String, Object>> rows = sqlExecutionService.executeQuery(generatedSql);
        return new TextToSqlResponse(generatedSql, rows);
    }

    private String normalizeSql(String sql) {
        return sql
                .replaceAll("\\s+", " ")
                .trim();
    }


}
