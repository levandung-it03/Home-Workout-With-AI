package com.restproject.backend.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restproject.backend.annotations.dev.Constructors;
import com.restproject.backend.annotations.dev.CoreEngines;
import com.restproject.backend.annotations.dev.Overload;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockAuthRequestBuilders {
    final ObjectMapper objectMapper;
    HashMap<String, String> jwtTokens;
    String content;

    final String adminUrl = "/api/private/admin";
    final String userUrl = "/api/private/user";

    @Constructors
    @Autowired
    public MockAuthRequestBuilders(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setJwtTokens(HashMap<String, String> jwtTokens) {
        this.jwtTokens = jwtTokens;
    }

    public <T> MockAuthRequestBuilders setContent(T object) throws JsonProcessingException {
        this.content = objectMapper.writeValueAsString(object);
        return this;
    }

    @Overload
    public MockAuthRequestBuilders setContent(String json) {
        this.content = json;
        return this;
    }

    public <T> MockAuthRequestBuilders replaceFieldOfContent(String name, T newValue) throws JsonProcessingException {
        Map<String, Object> contentAsMap = objectMapper.readValue(this.content, Map.class);
        contentAsMap.put(name, newValue);
        this.content = objectMapper.writeValueAsString(contentAsMap);
        return this;
    }

    public <T> MockHttpServletRequestBuilder buildAdminRequestWithContent(HttpMethod method, String uri) {
        return this.buildRequestWithContent(method, "root_accessToken", adminUrl, uri);
    }

    public MockHttpServletRequestBuilder buildAdminRequestNonContent(HttpMethod method, String uri) {
        return this.buildRequestNonContent(method, "root_accessToken", adminUrl, uri);
    }

    public <T> MockHttpServletRequestBuilder buildUserRequestWithContent(HttpMethod method, String uri) {
        return this.buildRequestWithContent(method, "user_accessToken", userUrl, uri);
    }

    public MockHttpServletRequestBuilder buildUserRequestNonContent(HttpMethod method, String uri) {
        return this.buildRequestNonContent(method, "user_accessToken", userUrl, uri);
    }

    @CoreEngines
    public MockHttpServletRequestBuilder buildRequestWithContent(HttpMethod method, String tokenName, String pref,
                                                                 String uri) {
        return MockMvcRequestBuilders.request(method, pref + uri).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + jwtTokens.get(tokenName))
            .content(this.content);
    }

    @CoreEngines
    public MockHttpServletRequestBuilder buildRequestNonContent(HttpMethod method, String tokenName, String pref,
                                                                String uri) {
        return MockMvcRequestBuilders.request(method, pref + uri).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + jwtTokens.get(tokenName));
    }
}
