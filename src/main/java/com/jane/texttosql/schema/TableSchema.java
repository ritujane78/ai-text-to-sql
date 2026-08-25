package com.jane.texttosql.schema;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class TableSchema {

    private final String tableName;

    private final List<String> columns;
}
