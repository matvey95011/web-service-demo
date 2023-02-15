package ru.example.java.spring.demo.app.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "email", unique = true, nullable = false, length = 80)
    private String email;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "user")
    private Profile profile;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<Phone> phoneList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
