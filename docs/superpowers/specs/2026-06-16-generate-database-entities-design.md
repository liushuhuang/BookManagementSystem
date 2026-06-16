# Generate Database Entities Design

## Scope

Generate Java entity classes for existing MySQL tables in the `bms` schema that do not already have entity classes. Keep the existing `SysUser.java` unchanged.

Tables to generate:

- `book` -> `Book`
- `book_borrow` -> `BookBorrow`
- `book_category` -> `BookCategory`
- `book_stock` -> `BookStock`
- `sys_role` -> `SysRole`
- `sys_user_role` -> `SysUserRole`

Existing table to leave unchanged:

- `sys_user` -> `SysUser`

## Architecture

All generated classes live in `src/main/java/com/book/domain/entity` and follow the existing MyBatis-Plus style used by `SysUser.java`.

Each entity uses:

- `@Data`
- `@TableName("<table_name>")`
- `@TableId(type = IdType.AUTO)` on `id`
- `@TableField(fill = FieldFill.INSERT)` on `createTime`
- `@TableField(fill = FieldFill.INSERT_UPDATE)` on `updateTime`
- `@TableLogic` on `isDeleted` only for tables that contain the `is_deleted` column

## Type Mapping

- `bigint` -> `Long`
- `int` and `tinyint` -> `Integer`
- `varchar` and `text` -> `String`
- `decimal(10,2)` -> `BigDecimal`
- `date` -> `LocalDate`
- `datetime` -> `LocalDateTime`

## Error Handling

Entity generation does not add runtime behavior. Build verification should catch invalid imports, incorrect annotations, or type mismatches.

## Testing

Because these classes are schema-derived POJOs without behavior, verification is compile-based:

- Run `mvn test`
- Confirm the project compiles and existing tests, if any, still pass
