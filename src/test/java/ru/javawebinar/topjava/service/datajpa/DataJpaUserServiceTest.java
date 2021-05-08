package ru.javawebinar.topjava.service.datajpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.UserServiceTest;
import ru.javawebinar.topjava.util.Profiles;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest {
}
