package taskmanager.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import taskmanager.dao.Comment;
import taskmanager.dao.Task;
import taskmanager.dao.User;

import java.util.List;

public interface CommentRepo extends CrudRepository<Comment, Long>, PagingAndSortingRepository<Comment, Long> {
    Page<Comment> findAllByParentTaskId(Long taskId, Pageable pageable);
    List<Comment> findAllByParentTaskId(Long taskId);
}
