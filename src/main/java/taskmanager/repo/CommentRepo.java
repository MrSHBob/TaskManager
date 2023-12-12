package taskmanager.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import taskmanager.dao.Comment;

public interface CommentRepo extends CrudRepository<Comment, Long>, PagingAndSortingRepository<Comment, Long> {
}
