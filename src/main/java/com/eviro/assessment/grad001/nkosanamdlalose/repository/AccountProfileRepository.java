package com.eviro.assessment.grad001.nkosanamdlalose.repository;

import com.eviro.assessment.grad001.nkosanamdlalose.entity.AccountProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountProfileRepository extends JpaRepository<AccountProfile, Long> {
    AccountProfile getByNameAndSurname(String name, String surname);
}
