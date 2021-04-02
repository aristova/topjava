package ru.javawebinar.topjava.web;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private MealRestController mealRestController;
    private ConfigurableApplicationContext appCtx;

    @Override
    public void init() {
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        mealRestController = appCtx.getBean(MealRestController.class);
    }

    @Override
    public void destroy() {
        appCtx.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(
                id.isEmpty() ? null : Integer.parseInt(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (id.isEmpty()) {
            mealRestController.create(meal);
        } else {
            mealRestController.update(meal);
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                mealRestController.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(
                                null,
                                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                                "",
                                1000) :
                        mealRestController.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                String param = request.getParameter("filter");
                if (param != null && param.equals("filter")) {
                    LocalDate fromDate = request.getParameter("fromDate").isEmpty() ? null :
                            LocalDate.parse(request.getParameter("fromDate"));
                    LocalDate toDate = request.getParameter("toDate").isEmpty() ? null :
                            LocalDate.parse(request.getParameter("toDate"));
                    LocalTime fromTime = request.getParameter("fromTime").isEmpty() ? null :
                            LocalTime.parse(request.getParameter("fromTime"));
                    LocalTime toTime = request.getParameter("toTime").isEmpty() ? null :
                            LocalTime.parse(request.getParameter("toTime"));
                    request.setAttribute("meals", mealRestController.filterByDateTime(fromDate, toDate, fromTime, toTime));
                    request.getRequestDispatcher("/meals.jsp").forward(request, response);
                    break;
                }
                request.setAttribute("meals", mealRestController.getAll());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
