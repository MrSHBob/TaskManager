package taskmanager.task;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import taskmanager.TaskManagerApplication;
import taskmanager.enums.TaskPriorityEnum;
import taskmanager.enums.TaskStatusEnum;
import taskmanager.model.TaskRequest;
import taskmanager.repo.CommentRepo;
import taskmanager.repo.TaskRepo;
import taskmanager.repo.UserRepo;
import taskmanager.service.CommentService;
import taskmanager.service.TaskService;
import taskmanager.service.UserService;
import taskmanager.utils.Requests;

import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = TaskManagerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskTests {
    StringBuilder errors = null;

    String url = "http://localhost:8080";

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TaskRepo taskRepo;

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
    public void createTask() {
        before();
        String testUser = "usrT01";

        Requests req = new Requests(url);
        req.register(testUser, testUser);
        String token = req.authenticate(testUser, testUser).getBody().asString();

        TaskRequest tr = new TaskRequest();
        tr.setTitle("SomeTitle1");
        tr.setDescription("SomeDescription1");
        tr.setPriority(TaskPriorityEnum.HIGH.value);
        tr.setResponsiblePerson(userRepo.findByUsername(testUser).getId());

        Response resp1 = req.createTask(tr, token);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        Assertions.assertEquals(tr.getTitle(), map1.get("title"));
        Assertions.assertEquals(TaskStatusEnum.NEW.value, map1.get("status"));
        Assertions.assertEquals(tr.getPriority(), map1.get("priority"));
        Assertions.assertEquals(
                userRepo.findByUsername(testUser).getId().longValue(),
                Long.valueOf((int)((Map)map1.get("author")).get("id"))
        );
        Assertions.assertEquals(
                userRepo.findByUsername(testUser).getId().longValue(),
                Long.valueOf((int) ((Map)map1.get("responsiblePerson")).get("id"))
        );

        taskRepo.delete(taskRepo.findById(Long.valueOf((Integer) map1.get("id"))).get());
        userRepo.delete(userRepo.findByUsername(testUser));
        after();
    }

    //TODO 2 create invalid - empty title
    //TODO 3 create invalid - invalid priority
    //TODO 4 create invalid - invalid respPerson

    @Test
    @Order(5)
    public void updateTask() {
        before();
        String testUser = "usrT05";
        String testUser2 = "usrT05A";

        Requests req = new Requests(url);
        req.register(testUser, testUser);
        req.register(testUser2, testUser2);
        String token = req.authenticate(testUser, testUser).getBody().asString();

        TaskRequest tr = new TaskRequest();
        tr.setTitle("SomeTitle5");
        tr.setDescription("SomeDescription5");
        tr.setPriority(TaskPriorityEnum.LOW.value);
        tr.setResponsiblePerson(userRepo.findByUsername(testUser).getId());

        Response resp1 = req.createTask(tr, token);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        TaskRequest trNew = new TaskRequest();
        trNew.setId(Long.valueOf((int)map1.get("id")));
        trNew.setTitle("SomeNewTitle5");
        trNew.setDescription("SomeNewDescription5");
        trNew.setStatus(TaskStatusEnum.IN_PROCESS.value);
        trNew.setPriority(TaskPriorityEnum.CRITICAL.value);
        trNew.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());
        trNew.setAuthor(userRepo.findByUsername(testUser).getId());

        Response resp2 = req.updateTask(trNew, token);
        Map map2 = Requests.getMapFromJsonBody(resp2.getBody().asString());

        Assertions.assertEquals(trNew.getTitle(), map2.get("title"));
        Assertions.assertEquals(trNew.getStatus(), map2.get("status"));
        Assertions.assertEquals(trNew.getPriority(), map2.get("priority"));
        Assertions.assertEquals(
                userRepo.findByUsername(testUser).getId().longValue(),
                Long.valueOf((int)((Map)map2.get("author")).get("id"))
        );
        Assertions.assertEquals(
                userRepo.findByUsername(testUser2).getId().longValue(),
                Long.valueOf((int) ((Map)map2.get("responsiblePerson")).get("id"))
        );

        taskRepo.delete(taskRepo.findById(Long.valueOf((Integer) map2.get("id"))).get());
        userRepo.delete(userRepo.findByUsername(testUser));
        userRepo.delete(userRepo.findByUsername(testUser2));
        after();
    }

    @Test
    @Order(6)
    public void updateTaskEmptyId() {
        before();
        String testUser = "usrT06";
        String testUser2 = "usrT06A";

        Requests req = new Requests(url);
        req.register(testUser, testUser);
        req.register(testUser2, testUser2);
        String token = req.authenticate(testUser, testUser).getBody().asString();

        TaskRequest tr = new TaskRequest();
        tr.setTitle("SomeTitle6");
        tr.setDescription("SomeDescription6");
        tr.setPriority(TaskPriorityEnum.LOW.value);
        tr.setResponsiblePerson(userRepo.findByUsername(testUser).getId());

        Response resp1 = req.createTask(tr, token);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        TaskRequest trNew = new TaskRequest();
        trNew.setTitle("SomeNewTitle6");
        trNew.setDescription("SomeNewDescription6");
        trNew.setStatus(TaskStatusEnum.IN_PROCESS.value);
        trNew.setPriority(TaskPriorityEnum.CRITICAL.value);
        trNew.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());
        trNew.setAuthor(userRepo.findByUsername(testUser).getId());

        Response resp2 = req.updateTask(trNew, token);
        String body = resp2.getBody().asString();

        Assertions.assertEquals(400, resp2.getStatusCode());
        Assertions.assertTrue(body.contains("The given id must not be null"), "Message missed.");

        taskRepo.delete(taskRepo.findById(Long.valueOf((Integer) map1.get("id"))).get());
        userRepo.delete(userRepo.findByUsername(testUser));
        userRepo.delete(userRepo.findByUsername(testUser2));
        after();
    }

    @Test
    @Order(7)
    public void updateTaskEmptyTitle() {
        before();
        String testUser = "usrT07";
        String testUser2 = "usrT07A";

        Requests req = new Requests(url);
        req.register(testUser, testUser);
        req.register(testUser2, testUser2);
        String token = req.authenticate(testUser, testUser).getBody().asString();

        TaskRequest tr = new TaskRequest();
        tr.setTitle("SomeTitle7");
        tr.setDescription("SomeDescription7");
        tr.setPriority(TaskPriorityEnum.LOW.value);
        tr.setResponsiblePerson(userRepo.findByUsername(testUser).getId());

        Response resp1 = req.createTask(tr, token);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        TaskRequest trNew = new TaskRequest();
        trNew.setId(Long.valueOf((int)map1.get("id")));
        trNew.setDescription("SomeNewDescription7");
        trNew.setStatus(TaskStatusEnum.IN_PROCESS.value);
        trNew.setPriority(TaskPriorityEnum.CRITICAL.value);
        trNew.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());
        trNew.setAuthor(userRepo.findByUsername(testUser).getId());

        Response resp2 = req.updateTask(trNew, token);
        String body = resp2.getBody().asString();

        Assertions.assertEquals(400, resp2.getStatusCode());
        Assertions.assertTrue(body.contains("Title is required field."), "Message missed.");

        taskRepo.delete(taskRepo.findById(Long.valueOf((Integer) map1.get("id"))).get());
        userRepo.delete(userRepo.findByUsername(testUser));
        userRepo.delete(userRepo.findByUsername(testUser2));
        after();
    }

    //TODO 8 update invalid - by Author - invalid status
    //TODO 9 update invalid - by Author - invalid priority
    //TODO 10 update invalid - by Author - invalid respPerson

    @Test
    @Order(11)
    public void updateTaskByRespPers() {
        before();
        String testUser = "usrT11";
        String testUser2 = "usrT11A";

        Requests req = new Requests(url);
        req.register(testUser, testUser);
        req.register(testUser2, testUser2);
        String token1 = req.authenticate(testUser, testUser).getBody().asString();
        String token2 = req.authenticate(testUser2, testUser2).getBody().asString();

        TaskRequest tr = new TaskRequest();
        tr.setTitle("SomeTitle11");
        tr.setDescription("SomeDescription11");
        tr.setPriority(TaskPriorityEnum.LOW.value);
        tr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Response resp1 = req.createTask(tr, token1);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        TaskRequest trNew = new TaskRequest();
        trNew.setId(Long.valueOf((int)map1.get("id")));
        trNew.setTitle(tr.getTitle());
        trNew.setDescription(tr.getDescription());
        trNew.setStatus(TaskStatusEnum.IN_PROCESS.value);
        trNew.setPriority(tr.getPriority());
        trNew.setResponsiblePerson(tr.getResponsiblePerson());
        trNew.setAuthor(userRepo.findByUsername(testUser).getId());

        Response resp2 = req.updateTask(trNew, token2);
        Assertions.assertEquals(200, resp2.getStatusCode());

        Map map2 = Requests.getMapFromJsonBody(resp2.getBody().asString());
        Assertions.assertEquals(trNew.getStatus(), map2.get("status"));

        taskRepo.delete(taskRepo.findById(Long.valueOf((Integer) map1.get("id"))).get());
        userRepo.delete(userRepo.findByUsername(testUser));
        userRepo.delete(userRepo.findByUsername(testUser2));
        after();
    }

    @Test
    @Order(12)
    public void updateTaskByRespPersInvalidChange() {
        before();
        String testUser = "usrT12";
        String testUser2 = "usrT12A";

        Requests req = new Requests(url);
        req.register(testUser, testUser);
        req.register(testUser2, testUser2);
        String token1 = req.authenticate(testUser, testUser).getBody().asString();
        String token2 = req.authenticate(testUser2, testUser2).getBody().asString();

        TaskRequest tr = new TaskRequest();
        tr.setTitle("SomeTitle12");
        tr.setDescription("SomeDescription12");
        tr.setPriority(TaskPriorityEnum.LOW.value);
        tr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Response resp1 = req.createTask(tr, token1);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        TaskRequest trNew = new TaskRequest();
        trNew.setId(Long.valueOf((int)map1.get("id")));
        trNew.setTitle("SomeNewTitle12-updated");
        trNew.setDescription(tr.getDescription());
        trNew.setStatus(TaskStatusEnum.IN_PROCESS.value);
        trNew.setPriority(tr.getPriority());
        trNew.setResponsiblePerson(tr.getResponsiblePerson());
        trNew.setAuthor(userRepo.findByUsername(testUser).getId());

        Response resp2 = req.updateTask(trNew, token2);
        Assertions.assertEquals(400, resp2.getStatusCode());
        String body = resp2.getBody().asString();
        Assertions.assertTrue(body.contains("Update allowed only for Author"), "Message missed.");

        taskRepo.delete(taskRepo.findById(Long.valueOf((Integer) map1.get("id"))).get());
        userRepo.delete(userRepo.findByUsername(testUser));
        userRepo.delete(userRepo.findByUsername(testUser2));
        after();
    }

    @Test
    @Order(13)
    public void updateTaskStatusByEnemy() {
        before();
        String testUser = "usrT13";
        String testUser2 = "usrT13A";
        String testUser3 = "usrT13B";

        Requests req = new Requests(url);
        req.register(testUser, testUser);
        req.register(testUser2, testUser2);
        req.register(testUser3, testUser3);
        String token1 = req.authenticate(testUser, testUser).getBody().asString();
        String token3 = req.authenticate(testUser3, testUser3).getBody().asString();

        TaskRequest tr = new TaskRequest();
        tr.setTitle("SomeTitle13");
        tr.setDescription("SomeDescription13");
        tr.setPriority(TaskPriorityEnum.LOW.value);
        tr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Response resp1 = req.createTask(tr, token1);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        TaskRequest trNew = new TaskRequest();
        trNew.setId(Long.valueOf((int)map1.get("id")));
        trNew.setTitle(tr.getTitle());
        trNew.setDescription(tr.getDescription());
        trNew.setStatus(TaskStatusEnum.IN_PROCESS.value);
        trNew.setPriority(tr.getPriority());
        trNew.setResponsiblePerson(tr.getResponsiblePerson());
        trNew.setAuthor(userRepo.findByUsername(testUser).getId());

        Response resp2 = req.updateTask(trNew, token3);
        Assertions.assertEquals(400, resp2.getStatusCode());
        String body = resp2.getBody().asString();
        Assertions.assertTrue(body.contains("Update allowed only for Responsible Person"), "Message missed.");

        taskRepo.delete(taskRepo.findById(Long.valueOf((Integer) map1.get("id"))).get());
        userRepo.delete(userRepo.findByUsername(testUser));
        userRepo.delete(userRepo.findByUsername(testUser2));
        userRepo.delete(userRepo.findByUsername(testUser3));
        after();
    }

    @Test
    @Order(14)
    public void updateTaskTitleByEnemy() {
        before();
        String testUser = "usrT14";
        String testUser2 = "usrT14A";
        String testUser3 = "usrT14B";

        Requests req = new Requests(url);
        req.register(testUser, testUser);
        req.register(testUser2, testUser2);
        req.register(testUser3, testUser3);
        String token1 = req.authenticate(testUser, testUser).getBody().asString();
        String token3 = req.authenticate(testUser3, testUser3).getBody().asString();

        TaskRequest tr = new TaskRequest();
        tr.setTitle("SomeTitle14");
        tr.setDescription("SomeDescription14");
        tr.setPriority(TaskPriorityEnum.LOW.value);
        tr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Response resp1 = req.createTask(tr, token1);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        TaskRequest trNew = new TaskRequest();
        trNew.setId(Long.valueOf((int)map1.get("id")));
        trNew.setTitle("SomeNewTitle14-updated");
        trNew.setDescription(tr.getDescription());
        trNew.setStatus(TaskStatusEnum.NEW.value);
        trNew.setPriority(tr.getPriority());
        trNew.setResponsiblePerson(tr.getResponsiblePerson());
        trNew.setAuthor(userRepo.findByUsername(testUser).getId());

        Response resp2 = req.updateTask(trNew, token3);
        Assertions.assertEquals(400, resp2.getStatusCode());
        String body = resp2.getBody().asString();
        Assertions.assertTrue(body.contains("Update allowed only for Author"), "Message missed.");

        taskRepo.delete(taskRepo.findById(Long.valueOf((Integer) map1.get("id"))).get());
        userRepo.delete(userRepo.findByUsername(testUser));
        userRepo.delete(userRepo.findByUsername(testUser2));
        userRepo.delete(userRepo.findByUsername(testUser3));
        after();
    }
}
