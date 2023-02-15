package ru.example.java.spring.demo.app.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PROFILES")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NamedEntityGraph(name = "Profile",
        attributeNodes = {@NamedAttributeNode("user")
        })
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "cash")
    BigDecimal cash;

    @Column(name = "max_cash")
    BigDecimal maxCash;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    Long userId;
}
