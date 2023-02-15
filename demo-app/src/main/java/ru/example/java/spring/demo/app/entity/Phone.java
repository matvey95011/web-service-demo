package ru.example.java.spring.demo.app.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PHONES")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NamedEntityGraph(name = "Phone",
        attributeNodes = {@NamedAttributeNode("user")
        })
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "phone_value", length = 25, nullable = false)
    String value;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    Long userId;

}
