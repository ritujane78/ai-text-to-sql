package com.jane.texttosql.schema;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ColumnSchema {
    private final String name;
    private final String description;
}
