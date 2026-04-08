package com.bootcamp.credit.application.handler;

import com.bootcamp.credit.application.exception.GlobalExceptionHandler;
import com.bootcamp.credit.domain.model.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleRuntime() {

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/api/accounts/123");

        RuntimeException ex = new RuntimeException("Runtime error");

        ResponseEntity<ErrorResponse> resp = handler.handleRuntime(ex, req);

        assertEquals(400, resp.getStatusCodeValue());
        assertEquals("Runtime error", resp.getBody().getMessage());
        assertEquals("/api/accounts/123", resp.getBody().getPath());
    }

    @Test
    void testHandleGeneral() {

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/api/accounts");

        Exception ex = new Exception("General error");

        ResponseEntity<ErrorResponse> resp = handler.handleGeneral(ex, req);

        assertEquals(500, resp.getStatusCodeValue());
        assertEquals("General error", resp.getBody().getMessage());
    }
}