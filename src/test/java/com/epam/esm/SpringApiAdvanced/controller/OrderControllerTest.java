package com.epam.esm.SpringApiAdvanced.controller;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {OrderControllerTest.Initializer.class})
@Testcontainers
class OrderControllerTest {
    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("gift_test")
            .withUsername("root")
            .withPassword("admin")
            .withInitScript("create_test_db.sql");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + mysql.getJdbcUrl(),
                    "spring.datasource.username=" + mysql.getUsername(),
                    "spring.datasource.password=" + mysql.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    MockMvc mockMvc;

    @BeforeClass
    public static void setUp() {
        mysql.start();
    }

    @AfterClass
    public static void close() {
        mysql.close();
    }

    @Test
    void testFindAll() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.orderDtoList[0]").exists())
                .andExpect(jsonPath("$._embedded.orderDtoList[0].id").exists())
                .andExpect(jsonPath("$._embedded.orderDtoList[0].certificateId").exists())
                .andExpect(jsonPath("$._embedded.orderDtoList[0].orderPrice").exists())
                .andExpect(jsonPath("$._embedded.orderDtoList[0].orderDate").exists())
                .andExpect(jsonPath("$._links.self[0].href").value("http://localhost/order/1"))
                .andExpect(jsonPath("page.size").value(20))
                .andExpect(jsonPath("page.number").value(0))
                .andReturn();
        System.out.println(mvcResult);

    }

    @Test
    void testSaveOrder() throws Exception {
        mockMvc.perform(post("/order/").param("user", "1").param("certificate", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.certificateId").exists())
                .andExpect(jsonPath("$.orderDate").exists())
                .andExpect(jsonPath("$.orderPrice").exists())
                .andExpect(jsonPath("$.userId").exists());
    }

    @Test
    void testFindById() throws Exception {
        mockMvc.perform(get("/order/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.certificateId").exists())
                .andExpect(jsonPath("$.orderDate").exists())
                .andExpect(jsonPath("$.orderPrice").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/order/1"))
                .andExpect(jsonPath("$._links.findAll.href").value("http://localhost/order"));
    }
    @Test
    void testGetUserOrders() throws Exception {
        mockMvc.perform(get("/order/user/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.orderDtoList[0]").exists())
                .andExpect(jsonPath("$._embedded.orderDtoList[0].id").exists())
                .andExpect(jsonPath("$._embedded.orderDtoList[0].certificateId").exists())
                .andExpect(jsonPath("$._embedded.orderDtoList[0].orderPrice").exists())
                .andExpect(jsonPath("$._embedded.orderDtoList[0].orderDate").exists())
                .andExpect(jsonPath("$._links.self[0].href").value("http://localhost/order/user/1"))
                .andExpect(jsonPath("page.size").value(20))
                .andExpect(jsonPath("page.number").value(0));
    }
}

