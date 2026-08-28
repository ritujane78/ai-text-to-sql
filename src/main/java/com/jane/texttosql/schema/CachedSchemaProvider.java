package com.jane.texttosql.schema;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CachedSchemaProvider implements SchemaProvider {

    private final SchemaProvider dynamicSchemaProvider;

    private final List<TableSchema> myTables;
    private final List<Relationship> relationships;
    private final List<BusinessRule> businessRules;

    public CachedSchemaProvider(
            @Qualifier("dynamicSchemaProvider")
            SchemaProvider dynamicSchemaProvider
    ) {
        this.dynamicSchemaProvider = dynamicSchemaProvider;

        this.myTables = dynamicSchemaProvider.getTables();
        this.relationships = dynamicSchemaProvider.getRelationships();
        this.businessRules = dynamicSchemaProvider.getBusinessRules();
    }

    @Override
    public List<TableSchema> getTables() {
        return myTables;
    }

    @Override
    public List<Relationship> getRelationships() {
        return relationships;
    }

    @Override
    public List<BusinessRule> getBusinessRules() {
        return businessRules;
    }
}