package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int MEAL_ID = START_SEQ + 2;
    public static final int MEAL_ID_2 = START_SEQ + 3;
    public static final int MEAL_ID_3 = START_SEQ + 4;
    public static final int NOT_FOUND = 10;

    public static final Meal meal = new Meal(MEAL_ID, LocalDateTime.of(2021, Month.JANUARY, 1, 9, 0), "Завтрак", 300);
    public static final Meal meal2 = new Meal(MEAL_ID_2, LocalDateTime.of(2021, Month.JANUARY, 1, 14, 0), "Обед", 400);
    public static final Meal meal3 = new Meal(MEAL_ID_3, LocalDateTime.of(2021, Month.JANUARY, 5, 20, 0), "Ужин", 500);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2021, Month.JANUARY, 20, 11, 0), "Новый завтрак", 300);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(meal);
        updated.setDescription("UpdatedMeal");
        updated.setCalories(500);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
