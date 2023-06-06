package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.dto.TagDto;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {TagControllerTest.Initializer.class})
@Testcontainers
class TagControllerTest {
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

    @LocalServerPort
    private int port;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TagController tagController;

    @Before
    public void setUp() {
        mysql.start();
    }

    @After
    public void close() {
        mysql.close();
    }


    @Test
    public void contextLoads() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void testFindAll() {
        try {
            mockMvc.perform(get("/tag"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFindById() {
        try {
           mockMvc.perform(get("/tag/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFindByPartOfName() throws SQLException {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/tag/name/{partOfName}", "ar"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(jsonPath("$._embedded.tagDtoList[0].id").value("9"))
                    .andExpect(jsonPath("$._embedded.tagDtoList[0].name").value("arcu"))
                    .andExpect(jsonPath("$._links.self[0].href").value("http://localhost/tag/9"))
                    .andExpect(jsonPath("$.page.size").value(20))
                    .andExpect(jsonPath("$.page.totalElements").value(1))
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            System.out.println(contentAsString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetMostWidelyUsedTag() {
        try {
            mockMvc.perform(get("/tag/mostWidely"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(jsonPath("$.id").value(18))
                    .andExpect(jsonPath("$.name").value("gravida"))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost/tag/mostWidely"))
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdate() throws Exception {
        TagDto tagDto = new TagDto();
        tagDto.setId(8);
        tagDto.setName("Java");

        ObjectMapper objectMapper = new ObjectMapper();
        String tagJson = objectMapper.writeValueAsString(tagDto);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/tag/8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(tagJson);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.name").value("Java"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/tag/8"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testSave() throws Exception {
        TagDto tagDto = new TagDto();
        tagDto.setName("New tag");
        ObjectMapper objectMapper = new ObjectMapper();
        String tagJson = objectMapper.writeValueAsString(tagDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/tag/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(tagJson);
        this.mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andReturn();

    }
}

