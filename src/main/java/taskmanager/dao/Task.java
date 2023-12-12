package taskmanager.dao;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "TASK_TBL", schema = "task_manager_db")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime created;

    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime lastUpdateDate;

    private Integer version;

    private String title;

    private String description;

    private String status;

    private String priority;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AUTHOR_ID")
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RESPONSIBLE_PERSON_ID")
    private User responsiblePerson;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "PAR_TASK_ID")
    private List<Comment> comments;
}
