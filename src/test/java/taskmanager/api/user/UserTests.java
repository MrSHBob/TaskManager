package taskmanager.api.user;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import taskmanager.TaskManagerApplication;
import taskmanager.api.utils.Requests;
import taskmanager.model.user.LogonRequest;
import taskmanager.model.user.RegisterRequest;
import taskmanager.repo.UserRepo;
import taskmanager.utils.TestData;

import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = TaskManagerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {
    StringBuilder errors = null;

    @Autowired
    private UserRepo userRepo;

    void before() {
        errors = new StringBuilder();
    }
    void after() {
        if ((errors != null) && (!errors.isEmpty())) {
            Assertions.assertEquals("", errors.toString());
        }
    }

    @Test
    @Order(1)
    public void registrationTest() {
        before();
        String testUser = "usr01";
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername(testUser);
        rr.setPassword(testUser);

        Requests req = new Requests(TestData.BASE_URL);
        Map map1 = req.register(rr);

        Assertions.assertEquals(testUser, map1.get("username").toString());

        userRepo.delete(userRepo.findByUsername(testUser));
        after();
    }

    @Test
    @Order(2)
    public void registrationTestNullLogin() {
        before();
        String testUser = "usr02";
        RegisterRequest rr = new RegisterRequest();
        rr.setPassword(testUser);

        Requests req = new Requests(TestData.BASE_URL);
        Map map1 = req.register(rr);

        Assertions.assertEquals("must not be blank", map1.get("username").toString());

        after();
    }

    @Test
    @Order(3)
    public void registrationTestNullPassword() {
        before();
        String testUser = "usr03";
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername(testUser);

        Requests req = new Requests(TestData.BASE_URL);
        Map map1 = req.register(rr);

        Assertions.assertEquals("must not be blank", map1.get("password").toString());

        after();
    }

    @Test
    @Order(4)
    public void registrationTestEmptyLogin() {
        before();
        String testUser = "usr04";
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername("");
        rr.setPassword(testUser);

        Requests req = new Requests(TestData.BASE_URL);
        Map map1 = req.register(rr);

        Assertions.assertEquals("must not be blank", map1.get("username").toString());

        after();
    }

    @Test
    @Order(5)
    public void registrationTestEmptyPassword() {
        before();
        String testUser = "usr05";
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername(testUser);
        rr.setPassword("");

        Requests req = new Requests(TestData.BASE_URL);
        Map map1 = req.register(rr);

        Assertions.assertEquals("must not be blank", map1.get("password").toString());

        after();
    }

    @Test
    @Order(6)
    public void authenticationTest() {
        before();
        String testUser = "usr06";
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername(testUser);
        rr.setPassword(testUser);

        Requests req = new Requests(TestData.BASE_URL);
        Map map1 = req.register(rr);

        LogonRequest lr = new LogonRequest();
        lr.setUsername(testUser);
        lr.setPassword(testUser);


        Map map2 = req.authenticate(lr);
        String token = map2.get("token").toString();

        Assertions.assertTrue(token.length() > 20, "Invalid token length - " + token.length());

        userRepo.delete(userRepo.findByUsername(testUser));
        after();
    }

}
