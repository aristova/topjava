package ru.javawebinar.topjava.service.jpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.MealServiceTest;
import ru.javawebinar.topjava.util.Profiles;

@ActiveProfiles(Profiles.JPA)
public class JpaMealServiceTest extends MealServiceTest {
}
