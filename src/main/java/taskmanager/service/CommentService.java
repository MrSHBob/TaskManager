package taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import taskmanager.dao.Comment;
import taskmanager.model.comment.CommentRequest;
import taskmanager.model.comment.CommentResponse;
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

    public Page<CommentResponse> getAllCommentsByTaskIdPaged (int page, int size, Long taskId) {
        Page<Comment> midResultPage = repo.findAllByParentTaskId(
                taskId,
                PageRequest.of(page, size, Sort.by("created")));
        return midResultPage.map(CommentResponse::new);
    }
}
