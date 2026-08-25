package com.jane.texttosql.controller;

import com.jane.texttosql.dto.TextToSqlRequest;
import com.jane.texttosql.dto.TextToSqlResponse;
import com.jane.texttosql.service.TextToSqlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TextToSqlController {

    private final TextToSqlService textToSqlService;

    @PostMapping("/text-to-sql")
    public TextToSqlResponse textToSql(@RequestBody TextToSqlRequest textToSqlRequest) {
        return textToSqlService.handle(textToSqlRequest);
    }
}
