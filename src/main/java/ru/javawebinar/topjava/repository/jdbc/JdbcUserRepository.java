package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private static List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, User> map = new HashMap<>();

        while (rs.next()) {
            Integer id = rs.getInt("id");

            User currentUser = map.get(id);

            if (!map.containsKey(id)) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setCaloriesPerDay(rs.getInt("calories_per_day"));
                user.setRegistered(rs.getTimestamp("registered"));
                user.setEnabled(rs.getBoolean("enabled"));
                if (rs.getString("role") != null) {
                    user.setRoles(Collections.singletonList(Role.valueOf(rs.getString("role"))));
                }
                map.put(id, user);

            } else {
                Set<Role> userRoles = currentUser.getRoles();
                userRoles.add(Role.valueOf(rs.getString("role")));
                currentUser.setRoles(userRoles);
            }
        }
        return new ArrayList<>(map.values());
    }

    private void deleteRoles(int userId) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", userId);
    }

    private void insertRoles(User user) {
        Set<Role> roles = user.getRoles();

        jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles (user_id, role) VALUES (?, ?)",
                roles,
                roles.size(),
                (ps, role) -> {
                    ps.setLong(1, user.id());
                    ps.setString(2, role.name());
                });
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            insertRoles(user);
            return user;
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        }
        deleteRoles(user.getId());
        insertRoles(user);
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        String sql = "SELECT  * FROM users u LEFT OUTER JOIN user_roles ur ON u.id=ur.user_id WHERE u.id=:id";
        SqlParameterSource param = new MapSqlParameterSource("id", id);
        List<User> list = namedParameterJdbcTemplate.query(sql, param, JdbcUserRepository::extractData);
        return DataAccessUtils.singleResult(list);
    }

    @Override
    public User getByEmail(String email) {
        String sql = "SELECT  * FROM users u JOIN user_roles ur ON u.id=ur.user_id WHERE email=:email";
        SqlParameterSource param = new MapSqlParameterSource("email", email);
        List<User> list = namedParameterJdbcTemplate.query(sql, param, JdbcUserRepository::extractData);
        return DataAccessUtils.singleResult(list);
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT u.id, u.name, u.email, u.registered, u.calories_per_day, u.password, u.enabled, ur.role FROM users u JOIN user_roles ur ON u.id=ur.user_id " +
                " ORDER BY name, email";
        return jdbcTemplate.query(sql, JdbcUserRepository::extractData);
    }
}
