package com.book;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class ApiTestSupport {

    @Autowired
    protected TestRestTemplate rest;

    @Autowired
    protected JdbcTemplate jdbc;

    protected final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void baseReset() {
        jdbc.execute("delete from fine_record");
        jdbc.execute("delete from borrow_record");
        jdbc.execute("delete from book_copy");
        jdbc.execute("delete from book_info");
        jdbc.execute("delete from book_category");
        jdbc.execute("delete from sys_user_role");
        jdbc.execute("delete from sys_role");
        jdbc.execute("delete from sys_user");
        jdbc.update("insert into sys_role(id, role_name, role_code, status, is_deleted) values (1, '超级管理员', 'admin', 1, 0)");
        jdbc.update("insert into sys_role(id, role_name, role_code, status, is_deleted) values (2, '图书管理员', 'librarian', 1, 0)");
        jdbc.update("insert into sys_role(id, role_name, role_code, status, is_deleted) values (3, '普通读者', 'reader', 1, 0)");
        seedUser(1L, "admin_test", "管理员", "admin", 1, 5);
        seedUser(2L, "librarian_test", "馆员", "librarian", 1, 5);
        seedUser(3L, "reader_test", "读者", "reader", 1, 5);
        seedUser(4L, "disabled_reader", "禁用读者", "reader", 0, 5);
    }

    protected void seedUser(Long id, String username, String realName, String roleCode, int status, int maxBorrowCount) {
        jdbc.update(
                "insert into sys_user(id, username, password, real_name, reader_no, max_borrow_count, status, is_deleted) values (?, ?, ?, ?, ?, ?, ?, 0)",
                id, username, encoder.encode("123456"), realName, "R" + id, maxBorrowCount, status
        );
        Long roleId = jdbc.queryForObject("select id from sys_role where role_code = ?", Long.class, roleCode);
        jdbc.update("insert into sys_user_role(user_id, role_id) values (?, ?)", id, roleId);
    }

    protected String login(String username) {
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", "123456");
        Map<?, ?> response = rest.postForObject("/api/v1/auth/login", body, Map.class);
        assertThat(response).isNotNull();
        assertThat(response.get("code")).isEqualTo(200);
        Map<?, ?> data = (Map<?, ?>) response.get("data");
        return String.valueOf(data.get("accessToken"));
    }

    protected HttpEntity<Object> json(Object body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return new HttpEntity<>(body, headers);
    }
}
