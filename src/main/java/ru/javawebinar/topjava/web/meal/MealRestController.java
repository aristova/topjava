package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.Collection;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;

@Controller
public class MealRestController {

    @Autowired
    private MealService service;

    public Meal get(int id) {
        Meal meal = service.get(id);
        int authUserId = SecurityUtil.authUserId();
        if (meal.getUserId() != authUserId) {
            throw new NotFoundException("Not found entity with user.id = " + authUserId + " and meal.id = " + meal.getUserId());
        }
        return meal;
    }

    public void delete(int id) {
        Meal meal = service.get(id);
        int authUserId = SecurityUtil.authUserId();
        if (meal.getUserId() != authUserId) {
            throw new NotFoundException("Not found entity with user.id = " + authUserId + " and meal.id = " + meal.getUserId());
        }
        service.delete(id);
    }

    public Meal create(Meal meal) {
        return service.create(meal);
    }

    public void update(Meal meal) {
        int authUserId = SecurityUtil.authUserId();
        if (meal.getUserId() != authUserId) {
            throw new NotFoundException("Not found entity with user.id = " + authUserId + " and meal.id = " + meal.getUserId());
        }
        assureIdConsistent(meal, meal.getId());
        service.update(meal);
    }

    public Collection<Meal> getAll() {
        return service.getAll(SecurityUtil.authUserId());
    }

}