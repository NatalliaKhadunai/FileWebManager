package com.epam.training.dao.impl;

import com.epam.training.dao.UserDAO;
import com.epam.training.entity.Role;
import com.epam.training.entity.User;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

//TODO: data for user-role table isn't adding (constraint foreign key violation)
@DatabaseSetup(value = "classpath:/sample-data.xml",
        type = com.github.springtestdbunit.annotation.DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = "classpath:/sample-data.xml", type = com.github.springtestdbunit.annotation.DatabaseOperation.DELETE_ALL)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = UserDAOImplTest.SpringTestConfiguration.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class UserDAOImplTest {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Configuration
    @EnableTransactionManagement
    public static class SpringTestConfiguration {
        @Bean
        public UserDAO userDAO() {
            return new UserDAOImpl();
        }

        @Bean(name = "dataSource")
        public DriverManagerDataSource dataSource() {
            DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
            driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/tri_wln_trainingproject_test");
            driverManagerDataSource.setUsername("root");
            driverManagerDataSource.setPassword("12345");
            return driverManagerDataSource;
        }

        @Bean
        public JpaTransactionManager jpaTransactionManager() {
            JpaTransactionManager jtManager = new JpaTransactionManager(
                    entityManagerFactory().getObject());
            return jtManager;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
            lef.setDataSource(dataSource());
            lef.setJpaVendorAdapter(jpaVendorAdapter());
            lef.setPackagesToScan("com.epam.training.entity");
            return lef;
        }

        @Bean
        public JpaVendorAdapter jpaVendorAdapter() {
            HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
            hibernateJpaVendorAdapter.setShowSql(false);
            hibernateJpaVendorAdapter.setGenerateDdl(true);
            hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
            return hibernateJpaVendorAdapter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Test
    public void testSave() {
        User user = new User();
        user.setUsername("new-usr");
        user.setPasswordHash(passwordEncoder.encode("123"));
        user.addRole(Role.GUEST);

        User persistedUser = userDAO.save(user);
        assertNotNull(persistedUser.getId());

        List<User> userList = userDAO.getUsers();
        assertEquals(userList.size(), 7);

        userDAO.delete(user);
    }

    @Test
    public void testGetUser() {
        User user = userDAO.getUser("admin");
        assertNotNull(user);
        assertEquals(user.getUsername(), "admin");
        assertEquals(user.getPasswordHash(), "pass");
    }

    @Test
    public void testDelete() {
        User user = userDAO.getUser("usr1");
        userDAO.delete(user);

        List<User> userList = userDAO.getUsers();
        assertEquals(userList.size(), 5);
    }

    @Test
    public void testGetUsers() {
        List<User> userList = userDAO.getUsers();
        User user = userList.get(0);
        assertEquals(userList.size(), 6);
    }
}
