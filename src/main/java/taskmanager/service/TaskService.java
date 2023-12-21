package taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import taskmanager.dao.Task;
import taskmanager.enums.TaskPriorityEnum;
import taskmanager.enums.TaskStatusEnum;
import taskmanager.model.TaskRequest;
import taskmanager.model.task.CreateTaskRequest;
import taskmanager.model.task.DeleteTaskRequest;
import taskmanager.model.task.EditTaskRequest;
import taskmanager.model.task.TaskResponse;
import taskmanager.repo.CommentRepo;
import taskmanager.repo.TaskRepo;
import taskmanager.repo.UserRepo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class TaskService {
    @Autowired
    private TaskRepo repo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CommentRepo commentRepo;


    public Task create(
            CreateTaskRequest tr
    ) {
        Task t = new Task();
        t.setCreated(LocalDateTime.now());
        t.setLastUpdateDate(LocalDateTime.now());
        t.setVersion(0);
        t.setAuthor(userRepo.findById(tr.getAuthor()).get());
        t.setTitle(tr.getTitle());
        t.setDescription(tr.getDescription());
        t.setStatus(TaskStatusEnum.NEW.value);
        if ((tr.getPriority() == null) || (tr.getPriority().equals("")))
            tr.setPriority(TaskPriorityEnum.LOW.value);
        t.setPriority(tr.getPriority());
        t.setResponsiblePerson(userRepo.findById(tr.getResponsiblePerson()).get());
        return repo.save(t);
    }

    public Task update(
            EditTaskRequest tr,
            Long initiatorId
    ) {
        Task t = repo.findById(tr.getId()).get();

        t.setLastUpdateDate(LocalDateTime.now());
        t.setVersion(t.getVersion() + 1);
        if (!t.getTitle().equals(tr.getTitle())) {
            authorChange(t, initiatorId);
            t.setTitle(tr.getTitle());
        }
        if (!t.getDescription().equals(tr.getDescription())) {
            authorChange(t, initiatorId);
            t.setDescription(tr.getDescription());
        }
        if (!t.getStatus().equals(tr.getStatus())) {
            authorOrResponsiblePersonChange(t, initiatorId);
            t.setStatus(tr.getStatus());
        }
        if (!t.getPriority().equals(tr.getPriority())) {
            authorChange(t, initiatorId);
            t.setPriority(tr.getPriority());
        }
        if (!t.getResponsiblePerson().getId().equals(tr.getResponsiblePerson())) {
            authorChange(t, initiatorId);
            t.setResponsiblePerson(userRepo.findById(tr.getResponsiblePerson()).get());
        }
        return repo.save(t);
    }

    private void authorChange(
            Task t,
            Long initId
    ) {
        if (!t.getAuthor().getId().equals(initId))
            throw new RuntimeException("Update allowed only for Author");
    }
    private void authorOrResponsiblePersonChange(
            Task t,
            Long initId
    ) {
        if ((!t.getResponsiblePerson().getId().equals(initId)) && (!t.getAuthor().getId().equals(initId)))
            throw new RuntimeException("Update allowed only for Responsible Person");
    }

    public boolean delete(
            DeleteTaskRequest tr,
            Long initiatorId
    ) {
        Task t = repo.findById(tr.getId()).get();
        authorChange(t, initiatorId);
        try {
            commentRepo.deleteAll(commentRepo.findAllByParentTaskId(t.getId()));
            repo.delete(t);
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    public List<Task> getTasksByAuthor (Long authorId) {
        return repo.findAllByAuthor(userRepo.findById(authorId).get());
    }

    public List<Task> getTasksByRespPerson (Long respPerId) {
        return repo.findAllByResponsiblePerson(userRepo.findById(respPerId).get());
    }

    public List<Task> getAllTasks () {
        return StreamSupport.stream(repo.findAll().spliterator(), false).toList();
    }

    public Page<TaskResponse> getAllTasksPaged (int page, int size) {
        Page<Task> midResultPage = repo.findAll(PageRequest.of(page, size, Sort.by("created")));
        return midResultPage.map(TaskResponse::new);
    }

    public Page<TaskResponse> getTasksByAuthorPaged (Long author, int page, int size) {
        Page<Task> midResultPage = repo.findByAuthor(
                userRepo.findById(author).get(),
                PageRequest.of(page, size, Sort.by("created")));
        return midResultPage.map(TaskResponse::new);
    }

    public Page<TaskResponse> getTasksByRespPersonPaged (Long respPers,int page, int size) {
        Page<Task> midResultPage = repo.findByResponsiblePerson(
                userRepo.findById(respPers).get(),
                PageRequest.of(page, size, Sort.by("created")));
        return midResultPage.map(TaskResponse::new);
    }
}
