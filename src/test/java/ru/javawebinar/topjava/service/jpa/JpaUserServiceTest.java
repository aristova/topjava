package ru.javawebinar.topjava.service.jpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.UserServiceTest;
import ru.javawebinar.topjava.util.Profiles;

@ActiveProfiles(Profiles.JPA)
public class JpaUserServiceTest extends UserServiceTest {
}
