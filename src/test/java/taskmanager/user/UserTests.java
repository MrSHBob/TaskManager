package taskmanager.user;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import taskmanager.TaskManagerApplication;
import taskmanager.repo.UserRepo;
import taskmanager.utils.Requests;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = TaskManagerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {
    StringBuilder errors = null;

    String url = "http://localhost:8080";

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

        Requests req = new Requests(url);
        Response resp1 = req.register(testUser, testUser);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        Assertions.assertEquals(testUser, map1.get("username").toString());

        userRepo.delete(userRepo.findByUsername(testUser));
        after();
    }

    @Test
    @Order(2)
    public void registrationTestNullLogin() {
        before();
        String testUser = "usr02";

        Requests req = new Requests(url);
        Response resp1 = req.register(null, testUser);

        Assertions.assertEquals(400, resp1.getStatusCode());
        String body = resp1.getBody().asString();
        Assertions.assertTrue(body.contains("Invalid login/password"), "Message missed.");

        after();
    }

    @Test
    @Order(3)
    public void registrationTestNullPassword() {
        before();
        String testUser = "usr03";

        Requests req = new Requests(url);
        Response resp1 = req.register(testUser, null);

        Assertions.assertEquals(400, resp1.getStatusCode());
        String body = resp1.getBody().asString();
        Assertions.assertTrue(body.contains("Invalid login/password"), "Message missed.");

        after();
    }

    @Test
    @Order(4)
    public void registrationTestShortLogin() {
        before();
        String testUser = "usr04";

        Requests req = new Requests(url);
        Response resp1 = req.register("qwe", testUser);

        Assertions.assertEquals(400, resp1.getStatusCode());
        String body = resp1.getBody().asString();
        Assertions.assertTrue(body.contains("Invalid login/password"), "Message missed.");

        after();
    }

    @Test
    @Order(5)
    public void registrationTestShortPassword() {
        before();
        String testUser = "usr05";

        Requests req = new Requests(url);
        Response resp1 = req.register(testUser, "qwe");

        Assertions.assertEquals(400, resp1.getStatusCode());
        String body = resp1.getBody().asString();
        Assertions.assertTrue(body.contains("Invalid login/password"), "Message missed.");

        after();
    }

    @Test
    @Order(6)
    public void authenticationTest() {
        before();
        String testUser = "usr06";

        Requests req = new Requests(url);
        Response resp1 = req.register(testUser, testUser);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        Response resp2 = req.authenticate(testUser, testUser);
        String token = resp2.getBody().asString();

        Assertions.assertEquals(200, resp2.getStatusCode());
        Assertions.assertTrue(token.length() > 20, "Invalid token length - " + token.length());

        userRepo.delete(userRepo.findByUsername(testUser));
        after();
    }

}
