package taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskmanager.dao.Comment;
import taskmanager.dao.User;
import taskmanager.model.CommentRequest;
import taskmanager.repo.CommentRepo;
import taskmanager.repo.UserRepo;

import java.time.LocalDateTime;

@Service
public class CommentService {
    @Autowired
    private CommentRepo repo;
    @Autowired
    private UserRepo userRepo;


    public Comment create(
            CommentRequest cr
    ) {
        Comment c = new Comment();
        c.setCreated(LocalDateTime.now());
        c.setLastUpdateDate(LocalDateTime.now());
        c.setVersion(0);
        c.setParentTaskId(cr.getParentTaskId());
        c.setAuthor(userRepo.findById(cr.getAuthor()).get());
        c.setText(cr.getText());
        return repo.save(c);
    }
}
