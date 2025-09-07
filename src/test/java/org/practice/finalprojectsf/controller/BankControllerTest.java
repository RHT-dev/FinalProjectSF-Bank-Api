package org.practice.finalprojectsf.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.practice.finalprojectsf.entity.User;
import org.practice.finalprojectsf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureWebMvc
public class BankControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Очистка и создание тестового пользователя перед каждым тестом,
        // чтобы все тесты работали с "Пользователем"
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setUserId("testuser");
        testUser.setBalance(500.0); // Установка начального баланса
        userRepository.save(testUser);
    }
    @Test
    public void testGetBalance_UserNotFound() throws Exception {
        setup();
        mockMvc.perform(get("/api/bank/balance")
                .param("userId", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(-1))
                .andExpect(jsonPath("$.message").value("Пользователь не найден"));
    }

    @Test
    public void testPutMoney_Success() throws Exception {
        setup();
        String requestBody = "{\"userId\": \"testuser\", \"amount\": 100.0}";
        
        mockMvc.perform(post("/api/bank/put-money")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.message").value("Успех"));
    }

    @Test
    public void testPutMoney_InvalidAmount() throws Exception {
        setup();
        String requestBody = "{\"userId\": \"testuser\", \"amount\": -50.0}";
        
        mockMvc.perform(post("/api/bank/put-money")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.message").value("Сумма должна быть положительной"));
    }

    @Test
    public void testTakeMoney_InsufficientFunds() throws Exception {
        setup();
        String requestBody = "{\"userId\": \"testuser\", \"amount\": 1000.0}";
        
        mockMvc.perform(post("/api/bank/take-money")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.message").value("Недостаточно средств"));
    }

    @Test
    public void testGetOperations_UserNotFound() throws Exception {
        mockMvc.perform(get("/api/bank/get-operations")
                        .param("userId", "nonexistent")
                        .param("from", "2024-01-01T00:00:00")
                        .param("to", "2024-12-31T23:59:59"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.message").value("Пользователь не найден"));
    }
}

