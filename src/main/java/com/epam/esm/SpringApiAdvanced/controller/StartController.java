package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.repository.GiftCertificateRepository;
import com.epam.esm.SpringApiAdvanced.repository.TagRepository;
import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import com.epam.esm.SpringApiAdvanced.repository.entity.Tag;
import com.epam.esm.SpringApiAdvanced.service.GiftCertificateService;
import com.epam.esm.SpringApiAdvanced.service.OrderService;
import com.epam.esm.SpringApiAdvanced.service.UserService;
import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;
import com.epam.esm.SpringApiAdvanced.service.dto.OrderDto;
import com.epam.esm.SpringApiAdvanced.service.dto.UserDto;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@ResponseBody
@RequestMapping("")
@EnableHypermediaSupport(type = {EnableHypermediaSupport.HypermediaType.HAL})
public class StartController {

    private static final String INSERT_INTO_CERTIFICATE_TAG_QUERY = "INSERT INTO certificate_tag(certificate_id, tag_id) VALUES(?, ?);";
    private final Faker faker = new Faker();
    private final TagRepository tagRepository;
    private final GiftCertificateRepository giftCertificateRepository;
    private final UserService userService;
    private final GiftCertificateService giftCertificateService;
    private final OrderService orderService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StartController(TagRepository tagRepository, GiftCertificateRepository giftCertificateRepository, UserService userService, GiftCertificateService giftCertificateService, OrderService orderService, JdbcTemplate jdbcTemplate) {
        this.tagRepository = tagRepository;
        this.giftCertificateRepository = giftCertificateRepository;
        this.userService = userService;
        this.giftCertificateService = giftCertificateService;
        this.orderService = orderService;
        this.jdbcTemplate = jdbcTemplate;
    }

    class CertificateTag {
        private final Integer certificateId;
        private final Integer tagId;

        CertificateTag(Integer certificateId, Integer tagId) {
            this.certificateId = certificateId;
            this.tagId = tagId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CertificateTag that = (CertificateTag) o;
            return Objects.equals(certificateId, that.certificateId) && Objects.equals(tagId, that.tagId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(certificateId, tagId);
        }

        @Override
        public String toString() {
            return "CertificateTag{" +
                    "certificateId=" + certificateId +
                    ", tagId=" + tagId +
                    '}';
        }
    }

    @GetMapping("")
    public String starting() {
        String greetings = "Spring API Advance";
        return greetings;
    }

    @PostMapping("/fill_db")
    @Transactional
    public ResponseEntity<String> fillDB() {
        if (tagRepository.countAll() > 0) {
            return ResponseEntity.ok("DB already filled");
        }
        fillCertificateTag();
        fillUser();
        fillOrders();
        return ResponseEntity.ok("DB was filled");
    }

    private Set<Tag> fillTags() {
        Set<Tag> tagSet = new HashSet<>();
        while (tagSet.size() < 50) {
            String name = faker.funnyName().name() + faker.address().cityName();
            Tag tag = Tag.builder()
                    .name(name)
                    .build();
            tagSet.add(tag);
        }
        return tagSet;
    }

    private void fillUser() {
        Set<UserDto> userDtoSet = new HashSet<>();
        while (userDtoSet.size() < 100) {
            String firstName = faker.name().firstName();
            String domain = "@gmail.com";
            userDtoSet.add(UserDto.builder()
                    .email(firstName.toLowerCase() + domain)
                    .firstName(firstName)
                    .lastName(faker.name().lastName())
                    .password(faker.code().imei())
                    .build());
        }
        userDtoSet.forEach(userService::save);
    }

    private void fillOrders() {
        Set<OrderDto> orderDtoSet = new HashSet<>();
        while (orderDtoSet.size() < 1000) {
            Date orderDate = faker.date().between(new Date(2022, Calendar.JANUARY, 1), new Date(2023, Calendar.JULY, 1));
            LocalDate localOrderDate = orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int certificateId = faker.number().numberBetween(1, 100);
            int userId = faker.number().numberBetween(1, 100);
            GiftCertificateDto giftCertificateServiceById = giftCertificateService.findById(certificateId);
            orderDtoSet.add(OrderDto.builder()
                    .giftCertificateDto(GiftCertificateDto.builder()
                            .id(certificateId)
                            .build())
                    .orderDate(localOrderDate)
                    .orderPrice(giftCertificateServiceById.getPrice())
                    .userDto(UserDto.builder()
                            .id(userId)
                            .build())
                    .build());
        }
        for (OrderDto orderDto : orderDtoSet) {
            orderService.save(orderDto);
        }
    }

    private Set<GiftCertificate> fillCertificate() {
        Set<GiftCertificate> giftCertificates = new HashSet<>();
        while (giftCertificates.size() < 100) {
            Date dateFrom = Date.from(LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date dateTo = Date.from(LocalDate.of(2025, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            GiftCertificate certificate = GiftCertificate.builder()
                    .name(faker.book().title())
                    .description(faker.lorem().word())
                    .price(faker.number().numberBetween(10, 1000))
                    .duration(faker.number().numberBetween(1, 365))
                    .createDate(faker.date().between(dateFrom, dateTo).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .lastUpdateDate(faker.date().between(dateFrom, dateTo).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .build();
            giftCertificates.add(certificate);
        }
        return giftCertificates;
    }

    private void fillCertificateTag() {
        Set<Tag> tags = fillTags();
        Set<GiftCertificate> giftCertificates = fillCertificate();
        Random random = new Random();
        giftCertificateRepository.saveAll(giftCertificates);
        for (Tag tag : tags) {
            tagRepository.save(tag);
        }
        Set<CertificateTag> certificateTagSet = new HashSet<>();
        while (certificateTagSet.size() < 1000) {
            certificateTagSet.add(new CertificateTag(random.nextInt(giftCertificates.size() - 1), random.nextInt(tags.size() - 1)));
        }
        for (CertificateTag t : certificateTagSet)
            jdbcTemplate.update(INSERT_INTO_CERTIFICATE_TAG_QUERY, t.certificateId + 1, t.tagId + 1);
    }

}
