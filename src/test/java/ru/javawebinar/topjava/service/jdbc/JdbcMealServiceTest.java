package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.MealServiceTest;
import ru.javawebinar.topjava.util.Profiles;

@ActiveProfiles(Profiles.JDBC)
public class JdbcMealServiceTest extends MealServiceTest {
}
