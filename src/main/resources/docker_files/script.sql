CREATE USER 'usr'@'%' IDENTIFIED BY 'qwerty';
GRANT ALL PRIVILEGES ON *.* TO 'usr'@'%';
FLUSH PRIVILEGES;

create database IF NOT EXISTS task_manager_db;

SET GLOBAL time_zone = '+7:00';
commit;

use task_manager_db;
CREATE TABLE USER_TBL(
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         USER_NAME VARCHAR(50),
                         PASSWORD VARCHAR(50),
                         ROLE VARCHAR(50),
                         CREATED DATETIME(3),
                         LAST_UPDATE_DATE DATETIME(3),
                         VERSION INT,
                         UNIQUE KEY unique_user_name (USER_NAME)
);

CREATE TABLE TASK_TBL(
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           CREATED DATETIME(3),
                           LAST_UPDATE_DATE DATETIME(3),
                           VERSION INT,
                           TITLE VARCHAR(50),
                           DESCRIPTION VARCHAR(500),
                           STATUS VARCHAR(20),
                           PRIORITY VARCHAR(20),
                           AUTHOR_ID BIGINT,
                           RESPONSIBLE_PERSON_ID BIGINT,
                           FOREIGN KEY (AUTHOR_ID)
                               REFERENCES USER_TBL(id),
                           FOREIGN KEY (RESPONSIBLE_PERSON_ID)
                               REFERENCES USER_TBL(id)
);

CREATE TABLE COMMENTS_TBL(
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 CREATED DATETIME(3),
                                 LAST_UPDATE_DATE DATETIME(3),
                                 VERSION INT,
                                 PAR_TASK_ID BIGINT,
                                 AUTHOR_ID BIGINT,
                                 TEXT VARCHAR(500),
                                 FOREIGN KEY (AUTHOR_ID)
                                     REFERENCES USER_TBL(id),
                                 FOREIGN KEY (PAR_TASK_ID)
                                     REFERENCES TASK_TBL(id)
);
commit;
