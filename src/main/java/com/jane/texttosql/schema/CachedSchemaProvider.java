package com.jane.texttosql.schema;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CachedSchemaProvider implements SchemaProvider{
    private final SchemaProvider dynamicSchemaProvider;

    private final List<TableSchema> myTables;
    private final List<Relationship> relationships;
    private final List<BusinessRule> businessRules;

    public CachedSchemaProvider(@Qualifier("dynamicSchemaProvider") SchemaProvider dynamicSchemaProvider) {
        this.dynamicSchemaProvider = dynamicSchemaProvider;
        this.myTables = dynamicSchemaProvider.getTables();
        this.relationships = dynamicSchemaProvider.getRelationships();
        this.businessRules = dynamicSchemaProvider.getBusinessRules();
    }
    @Override
    public List<TableSchema> getTables() {
        return List.of();
    }

    @Override
    public List<Relationship> getRelationships() {
        return List.of();
    }

    @Override
    public List<BusinessRule> getBusinessRules() {
        return List.of();
    }
}
