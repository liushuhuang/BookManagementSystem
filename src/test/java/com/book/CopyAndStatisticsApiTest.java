package com.book;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CopyAndStatisticsApiTest extends ApiTestSupport {

    @Test
    void createsCopiesAndDashboardCountsThem() {
        String token = login("librarian_test");
        long categoryId = insertCategory();
        long bookId = insertBook(categoryId, 1);

        Map<String, Object> copy = new HashMap<>();
        copy.put("bookId", bookId);
        copy.put("copyCode", "COPY-A-001");
        copy.put("location", "A区-1架");
        Map<?, ?> createResponse = rest.postForObject("/api/v1/book-copies", json(copy, token), Map.class);

        assertThat(createResponse.get("code")).isEqualTo(200);

        Map<?, ?> dashboard = rest.exchange("/api/v1/statistics/dashboard", HttpMethod.GET, json(null, token), Map.class).getBody();
        assertThat(dashboard.get("code")).isEqualTo(200);
        Map<?, ?> data = (Map<?, ?>) dashboard.get("data");
        assertThat(data.get("bookCount")).isEqualTo(1);
        assertThat(data.get("copyCount")).isEqualTo(1);
        assertThat(data.get("availableCopyCount")).isEqualTo(1);
    }

    private long insertCategory() {
        jdbc.update("insert into book_category(id, category_name, category_code, status, is_deleted) values (10, '计算机', 'CS', 1, 0)");
        return 10L;
    }

    private long insertBook(long categoryId, int status) {
        jdbc.update("insert into book_info(id, isbn, book_name, author, publisher, category_id, status, is_deleted) values (20, '9787111000000', '测试图书', '作者', '出版社', ?, ?, 0)", categoryId, status);
        return 20L;
    }
}
