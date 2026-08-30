# AI Text-to-SQL

An AI-powered Text-to-SQL application that converts natural-language questions into executable MySQL queries using Spring Boot, Spring AI, and OpenAI.

The application combines database schema discovery, business rules, LLM-based SQL generation, AST-based SQL validation, query execution, and a lightweight browser UI.

## Overview

AI Text-to-SQL allows users to ask questions about a MySQL database using natural language instead of writing SQL manually.

For example:

> **Question:** Show the top 10 employees by salary for each department.

The application:

1. Reads the database schema and relationships.
2. Builds a prompt containing tables, columns, relationships, and business rules.
3. Sends the prompt to an OpenAI model through Spring AI.
4. Receives a SQL statement from the model.
5. Validates the generated SQL using JSQLParser.
6. Ensures that only permitted tables and columns are referenced.
7. Requires a `LIMIT` of at most 100 rows.
8. Executes the validated query against MySQL.
9. Returns the generated SQL and query results through the REST API.
10. Displays the SQL and results in a simple web UI.

---

## Key Features

- Natural-language database querying
- OpenAI integration through Spring AI
- MySQL database support
- Dynamic database schema discovery using `information_schema`
- Database relationship discovery from foreign keys
- Schema caching at application startup
- Business-rule-aware prompt generation
- SQL AST parsing and validation with JSQLParser
- `SELECT`-only query enforcement
- Allowed-table validation
- Allowed-column validation
- Mandatory result limit
- Maximum result limit of 100 rows
- REST API
- Browser-based demo UI
- Included NexaCorp sample database schema and seed data
- Maven Wrapper for easier setup

## Request Flow

### 1. User asks a question

The UI sends:

```http
POST /api/text-to-sql
Content-Type: application/json
```

with:

```json
{
  "question": "Show the top 10 employees by salary for each department"
}
```

### 2. Schema context is prepared

`PromptBuilder` obtains the schema from `CachedSchemaProvider`.

The prompt contains:

- table names
- table descriptions
- column names
- column descriptions
- relationships
- business rules
- SQL-generation instructions
- the user's question

The model is explicitly instructed to return only one SQL statement without markdown or explanation.

### 3. SQL is generated

`TextToSqlService` sends the prompt through Spring AI's `ChatClient`.

### 4. Generated SQL is validated

The SQL is parsed with JSQLParser before execution.

The validator checks:

- the statement is a `SELECT`
- referenced tables exist in the discovered schema
- referenced columns exist
- a `LIMIT` is present
- the `LIMIT` does not exceed 100

### 5. SQL is executed

After validation, `SqlExecutionService` executes the SQL using Spring's `JdbcTemplate`.

### 6. API response

The response contains:

```json
{
  "generatedSql": "SELECT ... LIMIT 10",
  "rows": [
    {
      "id": "...",
      "first_name": "...",
      "salary": 95000
    }
  ]
}
```

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 25 | Application language |
| Spring Boot 4.1.1 | Application framework |
| Spring AI 2.0.0 | LLM integration |
| OpenAI | SQL generation |
| MySQL | Database |
| Spring JDBC | SQL execution and metadata access |
| JSQLParser 5.3 | SQL parsing and AST validation |
| Lombok | Boilerplate reduction |
| Maven | Build and dependency management |
| HTML/CSS/JavaScript | Demo UI |

---
### Seed data

```text
mysql/nexacorp_seed_mysql.sql
```

contains sample records for testing the application.

---

## Prerequisites

Install:

- Java 25
- MySQL 8.x or compatible MySQL server
- An OpenAI API key

Maven does not need to be installed separately because the repository contains the Maven Wrapper.

---
### 2. Create the database

Start MySQL and run:

```bash
mysql -u root -p < mysql/nexacorp_schema_mysql.sql
```

Then load the seed data:

```bash
mysql -u root -p < mysql/nexacorp_seed_mysql.sql
```

Alternatively, run both SQL files through MySQL Workbench.

### 3. Configure OpenAI

Set the API key as an environment variable.

Linux/macOS:

```bash
export OPENAI_API_KEY="your-api-key"
```