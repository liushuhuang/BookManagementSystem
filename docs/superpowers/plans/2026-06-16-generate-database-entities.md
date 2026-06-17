# Generate Database Entities Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add Java entity classes for the six database tables that do not already have entities.

**Architecture:** Generate schema-derived MyBatis-Plus POJOs under `src/main/java/com/book/domain/entity`. Keep `SysUser.java` unchanged and verify the project still compiles.

**Tech Stack:** Java 8, Spring Boot 2.7, MyBatis-Plus 3.5.5, Lombok, Maven, MySQL.

---

### Task 1: Add Missing Entity Classes

**Files:**
- Create: `src/main/java/com/book/domain/entity/Book.java`
- Create: `src/main/java/com/book/domain/entity/BookBorrow.java`
- Create: `src/main/java/com/book/domain/entity/BookCategory.java`
- Create: `src/main/java/com/book/domain/entity/BookStock.java`
- Create: `src/main/java/com/book/domain/entity/SysRole.java`
- Create: `src/main/java/com/book/domain/entity/SysUserRole.java`

- [ ] **Step 1: Create schema-derived entities**

Use the table metadata from `bms` and the same annotation style as `SysUser.java`: Lombok `@Data`, MyBatis-Plus `@TableName`, `@TableId(type = IdType.AUTO)`, fill annotations for audit fields, and `@TableLogic` on tables with `is_deleted`.

- [ ] **Step 2: Verify compilation**

Run: `mvn test`
Expected: Maven build succeeds.

- [ ] **Step 3: Review working tree**

Run: `git status --short`
Expected: the six entity files and this plan are changed/untracked; pre-existing unrelated files remain untouched.
