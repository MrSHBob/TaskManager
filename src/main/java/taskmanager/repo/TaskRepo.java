package taskmanager.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import taskmanager.dao.Task;
import taskmanager.dao.User;

import java.util.List;

public interface TaskRepo extends CrudRepository<Task, Long>, PagingAndSortingRepository<Task, Long> {

    List<Task> findAllByAuthor(User user);
    List<Task> findAllByResponsiblePerson(User user);

    Page<Task> findByAuthor(User user, Pageable pageable);
    Page<Task> findByResponsiblePerson(User user, Pageable pageable);
}
