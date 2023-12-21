package taskmanager.api.comment;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import taskmanager.TaskManagerApplication;
import taskmanager.api.utils.Requests;
import taskmanager.enums.TaskPriorityEnum;
import taskmanager.model.comment.CommentRequest;
import taskmanager.model.task.CreateTaskRequest;
import taskmanager.model.task.DeleteTaskRequest;
import taskmanager.model.task.EditTaskRequest;
import taskmanager.model.user.LogonRequest;
import taskmanager.model.user.RegisterRequest;
import taskmanager.repo.UserRepo;
import taskmanager.utils.TestData;

import java.util.List;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = TaskManagerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentTests {
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
    public void getCommentTest() {
        before();
        String testUser1 = "usrCT1";
        String testUser2 = "usrCT1A";
        String testUser3 = "usrCT1B";

        Requests req = new Requests(TestData.BASE_URL);
        req.register(new RegisterRequest(testUser1, testUser1));
        req.register(new RegisterRequest(testUser2, testUser2));
        req.register(new RegisterRequest(testUser3, testUser3));
        String token1 = req.authenticate(new LogonRequest(testUser1, testUser1)).get("token").toString();
        String token2 = req.authenticate(new LogonRequest(testUser2, testUser2)).get("token").toString();
        String token3 = req.authenticate(new LogonRequest(testUser3, testUser3)).get("token").toString();

        CreateTaskRequest ctr = new CreateTaskRequest();
        ctr.setTitle("TitleForCommentsRequest");
        ctr.setDescription("DescriptionForCommentsRequest");
        ctr.setPriority(TaskPriorityEnum.LOW.value);
        ctr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Map map = req.createTask(ctr, token1);
        long ctrId = (long) (int) map.get("id");

        // add comments
        for (int i = 0; i < 20; i++) {
            CommentRequest cr = new CommentRequest();
            cr.setParentTaskId(ctrId);
            cr.setText("SomeCommentText - " + (i + 1));

            if (i < 6) {
                req.createComment(cr, token1);
            } else if (i > 14) {
                req.createComment(cr, token2);
            } else {
                req.createComment(cr, token3);
            }
        }

        // get comments for created task
        Map map1 = req.getCommentsByTaskId(0, 5, ctrId, token1);

        List<Map> list1 = (List) map1.get("content");

        Assertions.assertEquals(20, map1.get("totalElements"));

        list1.forEach(comment -> {
            Assertions.assertTrue(comment.get("text").toString().contains("SomeCommentText - "));
        });

        Map map2 = req.deleteTask(
                new DeleteTaskRequest(
                        ctrId,
                        null
                ), token1);
        Assertions.assertEquals(
                "Task deleted successful.", map2.get("message").toString(), "Message missed.");

        userRepo.delete(userRepo.findByUsername(testUser1));
        userRepo.delete(userRepo.findByUsername(testUser2));
        userRepo.delete(userRepo.findByUsername(testUser3));
        after();
    }
}
