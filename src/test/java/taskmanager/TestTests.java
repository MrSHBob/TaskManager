package taskmanager;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import taskmanager.dao.User;
import taskmanager.repo.UserRepo;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = TaskManagerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestTests {
    StringBuilder errors = null;

    @Autowired
    private UserRepo userRepo;

    private List<String> allowedValues = List.of(new String[]{"one", "two"});

    void before() {
        errors = new StringBuilder();
    }
    void after() {
        if ((errors != null) && (!errors.isEmpty())) {
            Assertions.assertEquals("", errors.toString());
        }
    }

//    @Test
//    @Order(1)
    public void test1() {
        before();

//        User b = userRepo.findAll().forEach();

        after();
    }

}
