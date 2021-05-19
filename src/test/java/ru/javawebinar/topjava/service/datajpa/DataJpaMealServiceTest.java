package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealServiceTest;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.util.Profiles;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.MEAL1_ID;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaMealServiceTest extends MealServiceTest {

    @Autowired
    protected UserService userService;

    @Test
    @Transactional
    public void getForUser() {
        Meal meal = service.getForUser(MEAL1_ID, USER_ID);
        User user = userService.get(USER_ID);
        System.out.println(user.getMeals());
        assertThat(user.getMeals(), hasItems(meal));
    }

    @Test
    @Transactional
    public void getForUserNotOwn() {
        assertThrows(NotFoundException.class, () -> service.getForUser(MEAL1_ID, ADMIN_ID));
    }
}
