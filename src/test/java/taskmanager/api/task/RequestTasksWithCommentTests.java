package taskmanager.api.task;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import taskmanager.TaskManagerApplication;
import taskmanager.api.utils.Requests;
import taskmanager.enums.TaskPriorityEnum;
import taskmanager.model.task.CreateTaskRequest;
import taskmanager.model.user.LogonRequest;
import taskmanager.model.user.RegisterRequest;
import taskmanager.repo.TaskRepo;
import taskmanager.repo.UserRepo;
import taskmanager.utils.TestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = TaskManagerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RequestTasksWithCommentTests {
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
    public void getTasksTest() {
        before();
        String testUser1 = "usrGT1";
        String testUser2 = "usrGT1A";

        Requests req = new Requests(TestData.BASE_URL);
        req.register(new RegisterRequest(testUser1, testUser1));
        req.register(new RegisterRequest(testUser2, testUser2));
        String token = req.authenticate(new LogonRequest(testUser1, testUser1)).get("token").toString();

        List<CreateTaskRequest> ctrs = new ArrayList<>();

        // add tasks
        for (int i = 0; i < 20; i++) {
            CreateTaskRequest ctr = new CreateTaskRequest();
            ctr.setTitle("TitleForRequest" + (i + 1));
            ctr.setDescription("DescriptionForRequest" + (i + 1));
            ctr.setPriority(TaskPriorityEnum.LOW.value);
            if (i > 14) {
                ctr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());
            } else {
                ctr.setResponsiblePerson(userRepo.findByUsername(testUser1).getId());
            }

            Map map = req.createTask(ctr, token);
            ctrs.add(ctr);
        }

        // get all Tasks paged ()
        Map map1 = req.getTasksPaginated(0, 5, token);

        Assertions.assertTrue((int) map1.get("totalElements") > 19, "Not enough amount of tasks");

        // get Tasks by Author (check that amount is 20)
        Map map2 = req.getTasksPaginatedByAuthor( 0, 5, userRepo.findByUsername(testUser1).getId(), token);
        List<Map> list2 = (List) map2.get("content");

        Assertions.assertEquals(20, (int) map2.get("totalElements"));

        // get by RespPerson TestUser2 - amount = 5
        Map map3 = req.getTasksPaginatedByRespPers(0, 5, userRepo.findByUsername(testUser2).getId(), token);
        List<Map> list3 = (List) map3.get("content");

        Assertions.assertEquals(5, (int) map3.get("totalElements"));

        taskRepo.findAllByAuthor(
                userRepo.findByUsername(testUser1)
        ).forEach(tsc -> {
            taskRepo.delete(taskRepo.findById(tsc.getId()).get());
        });
        userRepo.delete(userRepo.findByUsername(testUser1));
        userRepo.delete(userRepo.findByUsername(testUser2));
        after();
    }

}
