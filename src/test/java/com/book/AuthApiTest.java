package com.book;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthApiTest extends ApiTestSupport {

    @Test
    void loginReturnsTokenAndUserInfo() {
        Map<String, Object> body = new HashMap<>();
        body.put("username", "admin_test");
        body.put("password", "123456");

        Map<?, ?> response = rest.postForObject("/api/v1/auth/login", body, Map.class);

        assertThat(response.get("code")).isEqualTo(200);
        Map<?, ?> data = (Map<?, ?>) response.get("data");
        assertThat(data.get("accessToken")).isNotNull();
        assertThat(((Map<?, ?>) data.get("userInfo")).get("username")).isEqualTo("admin_test");
        assertThat(data.toString()).doesNotContain("password");
    }

    @Test
    void loginRejectsWrongPassword() {
        Map<String, Object> body = new HashMap<>();
        body.put("username", "admin_test");
        body.put("password", "wrong");

        Map<?, ?> response = rest.postForObject("/api/v1/auth/login", body, Map.class);

        assertThat(response.get("code")).isEqualTo(401);
    }

    @Test
    void loginRejectsDisabledUser() {
        Map<String, Object> body = new HashMap<>();
        body.put("username", "disabled_reader");
        body.put("password", "123456");

        Map<?, ?> response = rest.postForObject("/api/v1/auth/login", body, Map.class);

        assertThat(response.get("code")).isEqualTo(403);
    }

    @Test
    void meReturnsCurrentUser() {
        String token = login("librarian_test");

        Map<?, ?> response = rest.exchange("/api/v1/auth/me", HttpMethod.GET, json(null, token), Map.class).getBody();

        assertThat(response.get("code")).isEqualTo(200);
        Map<?, ?> data = (Map<?, ?>) response.get("data");
        assertThat(data.get("username")).isEqualTo("librarian_test");
        assertThat(data.toString()).contains("librarian");
    }
}
