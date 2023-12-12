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
            TaskRequest tr
    ) {
        createValidation(tr);

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

    private void createValidation(
            TaskRequest tr
    ) {
        if ((tr.getTitle() == null) || (tr.getTitle().isEmpty())) {
            throw new RuntimeException("Title is required field.");
        }
        if ( !TaskPriorityEnum.isItARightValue(tr.getPriority())) {
            throw new RuntimeException("Invalid Priority value");
        }
        try {
            userRepo.findById(tr.getResponsiblePerson());
        } catch (Exception e) {
            throw new RuntimeException("Responsible person not found");
        }
    }

    public Task update(
            TaskRequest tr,
            Long initiatorId
    ) {
        Task t = repo.findById(tr.getId()).get();

        updateValidation(tr, t);

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

    private void updateValidation(
            TaskRequest tr,
            Task t
    ) {
        if (!t.getAuthor().getId().equals(tr.getAuthor())) {
            throw new RuntimeException("Task update is not allowed.");
        }
        if ((tr.getTitle() == null) || (tr.getTitle().isEmpty())) {
            throw new RuntimeException("Title is required field.");
        }
        if ( !TaskStatusEnum.isItARightValue(tr.getStatus())) {
            throw new RuntimeException("Invalid Status value");
        }
        if ( !TaskPriorityEnum.isItARightValue(tr.getPriority())) {
            throw new RuntimeException("Invalid Priority value");
        }
        try {
            userRepo.findById(tr.getResponsiblePerson());
        } catch (Exception e) {
            throw new RuntimeException("Responsible person not found");
        }
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
            TaskRequest tr,
            Long initiatorId
    ) {
        Task t = repo.findById(tr.getId()).get();
        authorChange(t, initiatorId);
        try {
            commentRepo.deleteAll(t.getComments());
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

    public Page<Task> getAllTasksPaged (int page, int size) {
        Page<Task> resultPage = repo.findAll(PageRequest.of(page, size, Sort.by("created")));
        return resultPage;
    }

    public Page<Task> getTasksByAuthorPaged (Long author, int page, int size) {
        Page<Task> resultPage = repo.findByAuthor(
                userRepo.findById(author).get(),
                PageRequest.of(page, size, Sort.by("created")));
        return resultPage;
    }

    public Page<Task> getTasksByRespPersonPaged (Long respPers,int page, int size) {
        Page<Task> resultPage = repo.findByResponsiblePerson(
                userRepo.findById(respPers).get(),
                PageRequest.of(page, size, Sort.by("created"))
        );
        return resultPage;
    }
}
