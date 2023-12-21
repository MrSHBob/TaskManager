package taskmanager.api.task;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import taskmanager.TaskManagerApplication;
import taskmanager.api.utils.Requests;
import taskmanager.enums.TaskPriorityEnum;
import taskmanager.enums.TaskStatusEnum;
import taskmanager.model.task.CreateTaskRequest;
import taskmanager.model.task.EditTaskRequest;
import taskmanager.model.user.LogonRequest;
import taskmanager.model.user.RegisterRequest;
import taskmanager.repo.TaskRepo;
import taskmanager.repo.UserRepo;
import taskmanager.utils.TestData;

import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = TaskManagerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskTests {
    StringBuilder errors = null;

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

        Requests req = new Requests(TestData.BASE_URL);
        req.register(new RegisterRequest(testUser, testUser));
        String token = req.authenticate(new LogonRequest(testUser, testUser)).get("token").toString();

        CreateTaskRequest ctr = new CreateTaskRequest();
        ctr.setTitle("SomeTitle1");
        ctr.setDescription("SomeDescription1");
        ctr.setPriority(TaskPriorityEnum.HIGH.value);
        ctr.setResponsiblePerson(userRepo.findByUsername(testUser).getId());

        Map map1 = req.createTask(ctr, token);

        Assertions.assertEquals(ctr.getTitle(), map1.get("title"));
        Assertions.assertEquals(TaskStatusEnum.NEW.value, map1.get("status"));
        Assertions.assertEquals(ctr.getPriority(), map1.get("priority"));
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

        Requests req = new Requests(TestData.BASE_URL);
        req.register(new RegisterRequest(testUser, testUser));
        req.register(new RegisterRequest(testUser2, testUser2));
        String token = req.authenticate(new LogonRequest(testUser, testUser)).get("token").toString();

        CreateTaskRequest ctr = new CreateTaskRequest();
        ctr.setTitle("SomeTitle5");
        ctr.setDescription("SomeDescription5");
        ctr.setPriority(TaskPriorityEnum.LOW.value);
        ctr.setResponsiblePerson(userRepo.findByUsername(testUser).getId());

        Map map1 = req.createTask(ctr, token);

        EditTaskRequest etr = new EditTaskRequest();
        etr.setId(Long.valueOf((int)map1.get("id")));
        etr.setTitle("SomeNewTitle5");
        etr.setDescription("SomeNewDescription5");
        etr.setStatus(TaskStatusEnum.IN_PROCESS.value);
        etr.setPriority(TaskPriorityEnum.CRITICAL.value);
        etr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Map map2 = req.updateTask(etr, token);

        Assertions.assertEquals(etr.getTitle(), map2.get("title"));
        Assertions.assertEquals(etr.getStatus(), map2.get("status"));
        Assertions.assertEquals(etr.getPriority(), map2.get("priority"));
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

        Requests req = new Requests(TestData.BASE_URL);
        req.register(new RegisterRequest(testUser, testUser));
        req.register(new RegisterRequest(testUser2, testUser2));
        String token = req.authenticate(new LogonRequest(testUser, testUser)).get("token").toString();

        CreateTaskRequest ctr = new CreateTaskRequest();
        ctr.setTitle("SomeTitle6");
        ctr.setDescription("SomeDescription6");
        ctr.setPriority(TaskPriorityEnum.LOW.value);
        ctr.setResponsiblePerson(userRepo.findByUsername(testUser).getId());

        Map map1 = req.createTask(ctr, token);

        EditTaskRequest etr = new EditTaskRequest();
        etr.setTitle("SomeNewTitle6");
        etr.setDescription("SomeNewDescription6");
        etr.setStatus(TaskStatusEnum.IN_PROCESS.value);
        etr.setPriority(TaskPriorityEnum.CRITICAL.value);
        etr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Map map2 = req.updateTask(etr, token);

        Assertions.assertEquals("ID is required field", map2.get("id").toString());

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

        Requests req = new Requests(TestData.BASE_URL);
        req.register(new RegisterRequest(testUser, testUser));
        req.register(new RegisterRequest(testUser2, testUser2));
        String token = req.authenticate(new LogonRequest(testUser, testUser)).get("token").toString();

        CreateTaskRequest ctr = new CreateTaskRequest();
        ctr.setTitle("SomeTitle7");
        ctr.setDescription("SomeDescription7");
        ctr.setPriority(TaskPriorityEnum.LOW.value);
        ctr.setResponsiblePerson(userRepo.findByUsername(testUser).getId());

        Map map1 = req.createTask(ctr, token);

        EditTaskRequest etr = new EditTaskRequest();
        etr.setId(Long.valueOf((int)map1.get("id")));
        etr.setDescription("SomeNewDescription7");
        etr.setStatus(TaskStatusEnum.IN_PROCESS.value);
        etr.setPriority(TaskPriorityEnum.CRITICAL.value);
        etr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Map map2 = req.updateTask(etr, token);

        Assertions.assertEquals("Title is required field", map2.get("title").toString());

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

        Requests req = new Requests(TestData.BASE_URL);
        req.register(new RegisterRequest(testUser, testUser));
        req.register(new RegisterRequest(testUser2, testUser2));
        String token1 = req.authenticate(new LogonRequest(testUser, testUser)).get("token").toString();
        String token2 = req.authenticate(new LogonRequest(testUser, testUser)).get("token").toString();

        CreateTaskRequest ctr = new CreateTaskRequest();
        ctr.setTitle("SomeTitle11");
        ctr.setDescription("SomeDescription11");
        ctr.setPriority(TaskPriorityEnum.LOW.value);
        ctr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Map map1 = req.createTask(ctr, token1);

        EditTaskRequest etr = new EditTaskRequest();
        etr.setId(Long.valueOf((int)map1.get("id")));
        etr.setTitle("SomeNewTitle11");
        etr.setDescription("SomeNewDescription11");
        etr.setStatus(TaskStatusEnum.IN_PROCESS.value);
        etr.setPriority(TaskPriorityEnum.CRITICAL.value);
        etr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Map map2 = req.updateTask(etr, token2);

        Assertions.assertEquals(etr.getStatus(), map2.get("status"));

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

        Requests req = new Requests(TestData.BASE_URL);
        req.register(new RegisterRequest(testUser, testUser));
        req.register(new RegisterRequest(testUser2, testUser2));
        String token1 = req.authenticate(new LogonRequest(testUser, testUser)).get("token").toString();
        String token2 = req.authenticate(new LogonRequest(testUser2, testUser2)).get("token").toString();

        CreateTaskRequest ctr = new CreateTaskRequest();
        ctr.setTitle("SomeTitle12");
        ctr.setDescription("SomeDescription12");
        ctr.setPriority(TaskPriorityEnum.LOW.value);
        ctr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Map map1 = req.createTask(ctr, token1);

        EditTaskRequest etr = new EditTaskRequest();
        etr.setId(Long.valueOf((int)map1.get("id")));
        etr.setTitle("SomeNewTitle12-updated");
        etr.setDescription(ctr.getDescription());
        etr.setStatus(map1.get("status").toString());
        etr.setPriority(ctr.getPriority());
        etr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Map map2 = req.updateTask(etr, token2);

        Assertions.assertTrue(
                map2.get("errorMessage").toString().contains("Update allowed only for Author"), "Message missed");

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

        Requests req = new Requests(TestData.BASE_URL);
        req.register(new RegisterRequest(testUser, testUser));
        req.register(new RegisterRequest(testUser2, testUser2));
        req.register(new RegisterRequest(testUser3, testUser3));
        String token1 = req.authenticate(new LogonRequest(testUser, testUser)).get("token").toString();
        String token3 = req.authenticate(new LogonRequest(testUser3, testUser3)).get("token").toString();

        CreateTaskRequest ctr = new CreateTaskRequest();
        ctr.setTitle("SomeTitle13");
        ctr.setDescription("SomeDescription13");
        ctr.setPriority(TaskPriorityEnum.LOW.value);
        ctr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Map map1 = req.createTask(ctr, token1);

        EditTaskRequest etr = new EditTaskRequest();
        etr.setId(Long.valueOf((int)map1.get("id")));
        etr.setTitle(ctr.getTitle());
        etr.setDescription(ctr.getDescription());
        etr.setStatus(TaskStatusEnum.IN_PROCESS.value);
        etr.setPriority(TaskPriorityEnum.LOW.value);
        etr.setResponsiblePerson(ctr.getResponsiblePerson());

        Map map2 = req.updateTask(etr, token3);

        Assertions.assertTrue(
                map2.get("errorMessage").toString().contains("Update allowed only for Responsible Person"),
                "Message missed");

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

        Requests req = new Requests(TestData.BASE_URL);
        req.register(new RegisterRequest(testUser, testUser));
        req.register(new RegisterRequest(testUser2, testUser2));
        req.register(new RegisterRequest(testUser3, testUser3));
        String token1 = req.authenticate(new LogonRequest(testUser, testUser)).get("token").toString();
        String token3 = req.authenticate(new LogonRequest(testUser3, testUser3)).get("token").toString();

        CreateTaskRequest ctr = new CreateTaskRequest();
        ctr.setTitle("SomeTitle14");
        ctr.setDescription("SomeDescription14");
        ctr.setPriority(TaskPriorityEnum.LOW.value);
        ctr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Map map1 = req.createTask(ctr, token1);

        EditTaskRequest etr = new EditTaskRequest();
        etr.setId(Long.valueOf((int)map1.get("id")));
        etr.setTitle("SomeNewTitle14-updated");
        etr.setDescription(ctr.getDescription());
        etr.setStatus(map1.get("status").toString());
        etr.setPriority(ctr.getPriority());
        etr.setResponsiblePerson(ctr.getResponsiblePerson());

        Map map2 = req.updateTask(etr, token3);

        Assertions.assertTrue(
                map2.get("errorMessage").toString().contains("Update allowed only for Author"), "Message missed");

        taskRepo.delete(taskRepo.findById(Long.valueOf((Integer) map1.get("id"))).get());
        userRepo.delete(userRepo.findByUsername(testUser));
        userRepo.delete(userRepo.findByUsername(testUser2));
        userRepo.delete(userRepo.findByUsername(testUser3));
        after();
    }
}
