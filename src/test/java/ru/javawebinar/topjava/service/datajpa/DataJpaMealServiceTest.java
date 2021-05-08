package ru.javawebinar.topjava.service.datajpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.MealServiceTest;
import ru.javawebinar.topjava.util.Profiles;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaMealServiceTest extends MealServiceTest {
}
