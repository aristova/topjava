package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.javawebinar.topjava.model.Meal;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
public class JspMealController extends AbstractMealController {
    @RequestMapping(value = "/meals", method = RequestMethod.GET)
    public String mealList(Model model, HttpServletRequest request) {
        model.addAttribute("meals", super.getAll());
        return "meals";
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String show(Model model, HttpServletRequest request) {

        // TODO Get from the parent class  mealController.get(getId(request));
        Meal meal = super.get(super.getId(request));
        model.addAttribute("meal", meal);

        return "mealForm";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(Model model, HttpServletRequest request) {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.hasLength(request.getParameter("id"))) {
            super.update(meal, getId(request));
        } else {
            super.create(meal);
        }
        return "redirect:meals";
    }


}
