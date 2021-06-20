package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

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
        List<User> list = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            User currentUser = list.stream()
                    .filter(p -> p.getId() != null && p.getId().equals(id))
                    .findFirst().orElse(null);

            if (currentUser == null) {
                User e = new User();
                e.setId(rs.getInt("id"));
                e.setName(rs.getString("name"));
                e.setEmail(rs.getString("email"));
                e.setPassword(rs.getString("password"));
                e.setCaloriesPerDay(rs.getInt("calories_per_day"));
                e.setRegistered(rs.getTimestamp("registered"));
                e.setEnabled(rs.getBoolean("enabled"));
                if (rs.getString("role") != null) {
                    e.setRoles(Collections.singletonList(Role.valueOf(rs.getString("role"))));
                }
                list.add(e);
            } else {
                Set<Role> userRoles = currentUser.getRoles();
                userRoles.add(Role.valueOf(rs.getString("role")));
                currentUser.setRoles(userRoles);
            }
        }
        return list;
    }

    public int[] batchInsert(List<User> users, String sql) {

        return this.jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, users.get(i).getName());
                        ps.setString(2, users.get(i).getEmail());
                        ps.setString(3, users.get(i).getPassword());
                        ps.setDate(4, new java.sql.Date(users.get(i).getRegistered().getTime()));
                        ps.setBoolean(5, users.get(i).isEnabled());
                        ps.setInt(6, users.get(i).getCaloriesPerDay());
                        if (!users.get(i).isNew()) {
                            ps.setInt(7, users.get(i).getId());
                        }

                    }

                    public int getBatchSize() {
                        return users.size();
                    }
                });
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        StringBuilder sql = new StringBuilder("");
        List<User> users = Collections.singletonList(user);

        if (user.isNew()) {
            List<Integer> nextIdList = jdbcTemplate.queryForList("SELECT nextval('global_seq')", Integer.class);
            Integer nextId = nextIdList.get(0);
            Set<Role> roles = user.getRoles();
            sql = new StringBuilder("insert into users (id, name, email, password, registered, enabled, calories_per_day) values (currval('global_seq'), ?, ?, ?, ?, ?, ?); ");

            if (!roles.isEmpty()) {
                sql = new StringBuilder("with first_insert as (insert into users (id, name, email, password, registered, enabled, calories_per_day)" +
                        " values (currval('global_seq'), ?, ?, ?, ?, ?, ?)  returning id) " +
                        "insert into user_roles (user_id, role) values ");
                List<String> valuesList = new ArrayList<>();
                for (Role role : roles) {
                    String string = String.format("(currval('global_seq'), '%s')", role.name());
                    valuesList.add(string);
                }
                String valuesString = String.join(", ", valuesList);
                sql.append(valuesString);
            }

            batchInsert(users, sql.toString());
            user.setId(nextId);
            return user;

        }

        Set<Role> roles = user.getRoles();
        sql = new StringBuilder("update users set name = ?, email = ?, password = ?, registered = ?, enabled = ?, calories_per_day = ? where id = ?");

        if (!roles.isEmpty()) {
            jdbcTemplate.update("delete from user_roles where user_id=?", user.getId());

            sql = new StringBuilder("with first_update as (update users set name = ?, email = ?, password = ?, registered = ?, enabled = ?, calories_per_day = ? where id = ?) " +
                    "insert into user_roles (user_id, role) values ");
            List<String> valuesList = new ArrayList<>();
            for (Role role : roles) {
                String string = String.format("(%d, '%s')", user.getId(), role.name());
                valuesList.add(string);
            }
            String valuesString = String.join(", ", valuesList);
            sql.append(valuesString);
        }

        batchInsert(users, sql.toString());
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
