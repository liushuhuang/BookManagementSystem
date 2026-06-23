package com.book;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BookCatalogApiTest extends ApiTestSupport {

    @Test
    void librarianCanCreateCategoryAndBook() {
        String token = login("librarian_test");
        Long categoryId = createCategory(token, "计算机科学", "CS");

        Map<String, Object> book = new HashMap<>();
        book.put("isbn", "9787111111111");
        book.put("bookName", "Java 编程基础");
        book.put("author", "张三");
        book.put("publisher", "机械工业出版社");
        book.put("categoryId", categoryId);

        Map<?, ?> response = rest.postForObject("/api/v1/books", json(book, token), Map.class);

        assertThat(response.get("code")).isEqualTo(200);
        Integer count = jdbc.queryForObject("select count(*) from book_info where isbn='9787111111111'", Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void readerCannotCreateCategory() {
        String token = login("reader_test");
        Map<String, Object> category = new HashMap<>();
        category.put("categoryName", "文学艺术");
        category.put("categoryCode", "LIT");

        Map<?, ?> response = rest.postForObject("/api/v1/book-categories", json(category, token), Map.class);

        assertThat(response.get("code")).isEqualTo(403);
    }

    @Test
    void disabledCategoryCannotBeUsedForNewBook() {
        String token = login("librarian_test");
        Long categoryId = createCategory(token, "下架分类", "OFF");
        Map<String, Object> status = new HashMap<>();
        status.put("status", 0);
        rest.exchange("/api/v1/book-categories/" + categoryId + "/status", HttpMethod.PUT, json(status, token), Map.class);

        Map<String, Object> book = new HashMap<>();
        book.put("isbn", "9787111222222");
        book.put("bookName", "不可绑定分类图书");
        book.put("author", "李四");
        book.put("publisher", "测试出版社");
        book.put("categoryId", categoryId);

        Map<?, ?> response = rest.postForObject("/api/v1/books", json(book, token), Map.class);

        assertThat(response.get("code")).isEqualTo(422);
    }

    private Long createCategory(String token, String name, String code) {
        Map<String, Object> category = new HashMap<>();
        category.put("categoryName", name);
        category.put("categoryCode", code);
        Map<?, ?> response = rest.postForObject("/api/v1/book-categories", json(category, token), Map.class);
        assertThat(response.get("code")).isEqualTo(200);
        Map<?, ?> data = (Map<?, ?>) response.get("data");
        return Long.valueOf(String.valueOf(data.get("id")));
    }
}
