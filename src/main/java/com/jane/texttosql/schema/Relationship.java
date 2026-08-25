package com.jane.texttosql.schema;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Relationship {
    private final String fromTable;
    private final String toTable;
    private final String fromColumn;
    private final String toColumn;
}
