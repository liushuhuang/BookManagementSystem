package com.book;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BorrowReturnApiTest extends ApiTestSupport {

    @Test
    void borrowAndReturnUpdateCopyAndBorrowRecord() {
        String token = login("librarian_test");
        seedAvailableBookAndCopy(10L, 20L, 30L);

        Map<String, Object> borrow = new HashMap<>();
        borrow.put("userId", 3L);
        borrow.put("bookId", 20L);
        borrow.put("copyId", 30L);
        Map<?, ?> borrowResponse = rest.postForObject("/api/v1/borrow-records", json(borrow, token), Map.class);

        assertThat(borrowResponse.get("code")).isEqualTo(200);
        assertThat(jdbc.queryForObject("select status from book_copy where id=30", Integer.class)).isEqualTo(2);

        Map<?, ?> borrowData = (Map<?, ?>) borrowResponse.get("data");
        Long borrowId = Long.valueOf(String.valueOf(borrowData.get("id")));
        Map<String, Object> ret = new HashMap<>();
        ret.put("borrowId", borrowId);
        ret.put("copyStatus", 1);
        Map<?, ?> returnResponse = rest.postForObject("/api/v1/returns", json(ret, token), Map.class);

        assertThat(returnResponse.get("code")).isEqualTo(200);
        assertThat(jdbc.queryForObject("select status from book_copy where id=30", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("select status from borrow_record where id=?", Integer.class, borrowId)).isEqualTo(1);
    }

    @Test
    void duplicateActiveBookBorrowIsRejected() {
        String token = login("librarian_test");
        seedAvailableBookAndCopy(10L, 20L, 30L);
        jdbc.update("insert into book_copy(id, book_id, copy_code, status, is_deleted) values (31, 20, 'COPY-A-002', 1, 0)");
        jdbc.update("insert into borrow_record(id, borrow_no, user_id, book_id, copy_id, due_time, status) values (40, 'B202606230001', 3, 20, 30, CURRENT_TIMESTAMP, 0)");
        jdbc.update("update book_copy set status=2 where id=30");

        Map<String, Object> borrow = new HashMap<>();
        borrow.put("userId", 3L);
        borrow.put("bookId", 20L);
        borrow.put("copyId", 31L);
        Map<?, ?> response = rest.postForObject("/api/v1/borrow-records", json(borrow, token), Map.class);

        assertThat(response.get("code")).isEqualTo(422);
        assertThat(jdbc.queryForObject("select status from book_copy where id=31", Integer.class)).isEqualTo(1);
    }

    @Test
    void repeatedReturnIsRejected() {
        String token = login("librarian_test");
        seedAvailableBookAndCopy(10L, 20L, 30L);
        jdbc.update("insert into borrow_record(id, borrow_no, user_id, book_id, copy_id, due_time, return_time, status) values (40, 'B202606230002', 3, 20, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");

        Map<String, Object> ret = new HashMap<>();
        ret.put("borrowId", 40L);
        ret.put("copyStatus", 1);
        Map<?, ?> response = rest.postForObject("/api/v1/returns", json(ret, token), Map.class);

        assertThat(response.get("code")).isEqualTo(422);
    }

    private void seedAvailableBookAndCopy(Long categoryId, Long bookId, Long copyId) {
        jdbc.update("insert into book_category(id, category_name, category_code, status, is_deleted) values (?, '计算机', 'CS', 1, 0)", categoryId);
        jdbc.update("insert into book_info(id, isbn, book_name, author, publisher, category_id, status, is_deleted) values (?, '9787111000000', '测试图书', '作者', '出版社', ?, 1, 0)", bookId, categoryId);
        jdbc.update("insert into book_copy(id, book_id, copy_code, status, is_deleted) values (?, ?, 'COPY-A-001', 1, 0)", copyId, bookId);
    }
}
