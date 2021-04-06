package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {

    @Autowired
    private MealService service;

    public Meal get(int id) {
        return service.get(id, SecurityUtil.authUserId());
    }

    public void delete(int id) {
        service.delete(id, SecurityUtil.authUserId());
    }

    public Meal create(Meal meal) {
        checkNew(meal);
        return service.create(meal, SecurityUtil.authUserId());
    }

    public void update(Meal meal) {
        assureIdConsistent(meal, meal.getId());
        service.update(meal, SecurityUtil.authUserId());
    }

    public Collection<MealTo> getAll() {
        Collection<Meal> meals = service.getAll(SecurityUtil.authUserId());
        return MealsUtil.getTos(meals, SecurityUtil.authUserCaloriesPerDay());
    }

    public Object filterByDateTime(LocalDate fromDate, LocalDate toDate, LocalTime fromTime, LocalTime toTime) {
        Collection<Meal> meals = service.getFilteredDate(SecurityUtil.authUserId(), fromDate, toDate, fromTime, toTime);
        return MealsUtil.getFilteredTos(meals, SecurityUtil.authUserCaloriesPerDay(), fromTime, toTime);
    }
}