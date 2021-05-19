package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {

    @Transactional
    @Modifying
    @Query(name = Meal.DELETE)
    int delete(@Param("id") int id, @Param("userId") int userId);

    List<Meal> getByUserIdOrderByIdDesc(int userId);

    Meal getByIdAndUserId(int id, int userId);

    @Transactional(readOnly = true)
    @Modifying
    @Query(name = Meal.GET_BETWEEN)
    List<Meal> getBetweenHalfOpen(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("userId") int userId);

    @Transactional
    @Query(name = Meal.WITH_USER)
    Meal getForUser(
            @Param("id") int id,
            @Param("userId") int userId);
}
