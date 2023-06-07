package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {UserControllerTest.Initializer.class})
@Testcontainers
class UserControllerTest {
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
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.userDtoList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.userDtoList[0].firstName").value("Gwyn"))
                .andExpect(jsonPath("$._embedded.userDtoList[0].lastName").value("Bernhard"))
                .andExpect(jsonPath("$._embedded.userDtoList[0].email").value("gwyn@gmail.com"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.self[0].href").value("http://localhost/user/1"));
    }

    @Test
    void testFindById() throws Exception {
        mockMvc.perform(get("/user/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Gwyn"))
                .andExpect(jsonPath("$.lastName").value("Bernhard"))
                .andExpect(jsonPath("$.email").value("gwyn@gmail.com"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testFindByName() throws Exception {
        mockMvc.perform(get("/user/name/{name}", "Bernhard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.userDtoList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.userDtoList[0].firstName").value("Gwyn"))
                .andExpect(jsonPath("$._embedded.userDtoList[0].lastName").value("Bernhard"))
                .andExpect(jsonPath("$._embedded.userDtoList[0].email").value("gwyn@gmail.com"))
                .andExpect(jsonPath("$._links.self[1].href").exists());
    }

    @Test
    void testSave() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("new_email@mail.com")
                .firstName("FirstName")
                .lastName("LastName")
                .password("password")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(userDto);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(userJson);
        this.mockMvc.perform(builder)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("new_email@mail.com"))
                .andExpect(jsonPath("$.firstName").value("FirstName"))
                .andExpect(jsonPath("$.lastName").value("LastName"))
                .andExpect(jsonPath("$.password").exists());
     }
}