package ru.example.java.spring.demo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.example.java.spring.demo.app.entity.Phone;

public interface PhoneRepository extends JpaRepository<Phone, Long>, JpaSpecificationExecutor<Phone> {

    boolean existsPhoneByValue(String value);

}
