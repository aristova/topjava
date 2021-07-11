package ru.javawebinar.topjava;

import ru.javawebinar.topjava.to.MealTo;

import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;

public class MealToTestData {
    public static final TestMatcher<MealTo> MEALTO_MATCHER = TestMatcher.usingIgnoringFieldsComparator(MealTo.class, "");

    public static final MealTo mealto7 = new MealTo(meal7.getId(), meal7.getDateTime(), meal7.getDescription(), meal7.getCalories(), true);
    public static final MealTo mealto6 = new MealTo(meal6.getId(), meal6.getDateTime(), meal6.getDescription(), meal6.getCalories(), true);
    public static final MealTo mealto5 = new MealTo(meal5.getId(), meal5.getDateTime(), meal5.getDescription(), meal5.getCalories(), true);
    public static final MealTo mealto4 = new MealTo(meal4.getId(), meal4.getDateTime(), meal4.getDescription(), meal4.getCalories(), true);
    public static final MealTo mealto3 = new MealTo(meal3.getId(), meal3.getDateTime(), meal3.getDescription(), meal3.getCalories(), false);
    public static final MealTo mealto2 = new MealTo(meal2.getId(), meal2.getDateTime(), meal2.getDescription(), meal2.getCalories(), false);
    public static final MealTo mealto1 = new MealTo(meal1.getId(), meal1.getDateTime(), meal1.getDescription(), meal1.getCalories(), false);

    public static final List<MealTo> mealstos = List.of(mealto7, mealto6, mealto5, mealto4, mealto3, mealto2, mealto1);
}
