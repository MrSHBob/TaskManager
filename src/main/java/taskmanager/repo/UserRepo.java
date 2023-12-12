package taskmanager.repo;

import org.springframework.data.repository.CrudRepository;
import taskmanager.dao.User;

public interface UserRepo extends CrudRepository<User, Long> {

    User findByUsername(String name);

}
