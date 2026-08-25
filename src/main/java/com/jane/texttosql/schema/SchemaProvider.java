package com.jane.texttosql.schema;


import java.util.List;

public interface SchemaProvider {
    List<TableSchema> getTables();

    List<Relationship> getRelationships();
}
