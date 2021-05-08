package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.UserServiceTest;
import ru.javawebinar.topjava.util.Profiles;

@ActiveProfiles(Profiles.JDBC)
public class JdbcUserServiceTest extends UserServiceTest {
}
