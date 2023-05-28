package com.epam.esm.SpringApiAdvanced.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Data
@Table(name = "orders")
@Entity
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;
    private Integer userId;
    private Integer certificateId;
    private Integer orderPrice;
    private LocalDate orderDate;
//    @ManyToOne
//    @JoinColumn(name = "users_id")
//    private User user;
}
