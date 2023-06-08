package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.After;
import org.junit.Before;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {GiftCertificateControllerTest.Initializer.class})
@Testcontainers
class GiftCertificateControllerTest {
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

    @Before
    public void setUp() {
        mysql.start();
    }

    @After
    public void close() {
        mysql.close();
    }

    @Test
    public void testFindAll() throws Exception {
        this.mockMvc.perform(get("/certificate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._links.self[0].href").value("http://localhost/certificate/1"))
                .andExpect(jsonPath("$._embedded.giftCertificateDtoList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.giftCertificateDtoList[0].name").value("Alpha"));
    }

    @Test
    public void testFindById() throws Exception {
        this.mockMvc.perform(get("/certificate/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alpha"));
    }

    @Test
    public void testSave() throws Exception {
        GiftCertificateDto certificateDto = GiftCertificateDto.builder()
                .createDate(LocalDate.of(2023, 1, 1))
                .description("certificate description")
                .duration(100)
                .lastUpdateDate(LocalDate.of(2023, 2, 1))
                .name("New certificate")
                .price(55)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        String certificateJson = objectMapper.writeValueAsString(certificateDto);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/certificate/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(certificateJson);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("New certificate"))
                .andReturn();
    }

    @Test
    public void testUpdate() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/certificate/update/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Time\",\"description\":\"Time description\",\"price\":880,\"duration\":70,\"createDate\":\"06/02/2022\",\"lastUpdateDate\":\"06/07/2023\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Time"))
                .andExpect(jsonPath("$.description").value("Time description"))
                .andExpect(jsonPath("$.price").value(880))
                .andExpect(jsonPath("$.duration").value(70));
    }

    @Test
    public void testFindBySeveralTags() throws Exception {
        mockMvc.perform(get("/certificate/tags")
                        .param("name", "Suspendisse"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.giftCertificateDtoList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.giftCertificateDtoList[0].name").value("Beta"))
                .andExpect(jsonPath("$._embedded.giftCertificateDtoList[0].description").value("Evia"))
                .andExpect(jsonPath("$._embedded.giftCertificateDtoList[0].price").value(80))
                .andExpect(jsonPath("$._embedded.giftCertificateDtoList[0].duration").value(71))
                .andExpect(jsonPath("$._links.find_by_id[0].href").value("http://localhost/certificate/2"))
                .andExpect(jsonPath("$._links.find_all.href").value("http://localhost/certificate"))
                .andExpect(jsonPath("$._links.prev").doesNotExist())
                .andExpect(jsonPath("$._links.next").exists());
    }
}

