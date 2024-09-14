SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';


CREATE SCHEMA IF NOT EXISTS `kanbanIT` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `kanbanIT`;

-- Create boards table
CREATE TABLE IF NOT EXISTS `kanbanIT`.`boards` (
  `boardId` CHAR(10) ,
  `boardname` VARCHAR(120) NOT NULL,
  `userId` VARCHAR(36) , 
  PRIMARY KEY (`boardId`),
  INDEX `userId_index` (`userId` ASC)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- Create statustasks table
CREATE TABLE IF NOT EXISTS `kanbanIT`.`statustasks` (
  `statusId` INT NOT NULL AUTO_INCREMENT,
  `statusname` VARCHAR(50) NOT NULL DEFAULT 'NO_STATUS',
  `statusdescription` VARCHAR(200) NULL DEFAULT NULL,
  `createdOn` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedOn` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `boardId` CHAR(10),
  PRIMARY KEY (`statusId`),
  INDEX `boardId_index` (`boardId` ASC),
  CONSTRAINT `fk_statustasks_boards`
    FOREIGN KEY (`boardId`)
    REFERENCES `kanbanIT`.`boards` (`boardId`)
    ON DELETE CASCADE
)
ENGINE = InnoDB
AUTO_INCREMENT = 32
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- Create tasklimit table
CREATE TABLE IF NOT EXISTS `kanbanIT`.`tasklimit` (
  `tasklimitId` INT NOT NULL AUTO_INCREMENT,
  `maximumTask` INT NOT NULL DEFAULT '10',
  `isLimit` TINYINT(1) NOT NULL DEFAULT '0',
  `boardId` CHAR(10) ,
  PRIMARY KEY (`tasklimitId`),
  UNIQUE INDEX `tasklimitId_UNIQUE` (`tasklimitId` ASC) VISIBLE,
  INDEX `boardId_index` (`boardId` ASC),
  CONSTRAINT `fk_tasklimit_boards`
    FOREIGN KEY (`boardId`)
    REFERENCES `kanbanIT`.`boards` (`boardId`)
    ON DELETE CASCADE
)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- Create tasks table
CREATE TABLE IF NOT EXISTS `kanbanIT`.`tasks` (
  `taskId` INT NOT NULL AUTO_INCREMENT,
  `taskTitle` TEXT NOT NULL,
  `taskDescription` TEXT NULL DEFAULT NULL,
  `taskAssignees` TINYTEXT NULL DEFAULT NULL,
  `createdOn` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedOn` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `statusId` INT NOT NULL DEFAULT '1',
  `boardId` CHAR(10) ,
  PRIMARY KEY (`taskId`),
  UNIQUE INDEX `taskId_UNIQUE` (`taskId` ASC) VISIBLE,
  INDEX `fk_tasks_statustasks_idx` (`statusId` ASC) VISIBLE,
  INDEX `boardId_index` (`boardId` ASC),
  CONSTRAINT `fk_tasks_statustasks`
    FOREIGN KEY (`statusId`)
    REFERENCES `kanbanIT`.`statustasks` (`statusId`),
  CONSTRAINT `fk_tasks_boards`
    FOREIGN KEY (`boardId`)
    REFERENCES `kanbanIT`.`boards` (`boardId`)
    ON DELETE CASCADE
)
ENGINE = InnoDB
AUTO_INCREMENT = 121
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- Re-enable checks
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Insert statustasks
-- -----------------------------------------------------
INSERT INTO kanbanIT.statustasks (statusid, statusname, statusdescription) VALUES
(1, 'No Status', 'A status has not been assigned'),
(2, 'To Do', 'The task is included in the project'),
(3, 'In Progress', 'The task is being worked on'),
(4, 'Reviewing', 'The task is being reviewed'),
(5, 'Testing', 'The task is being tested'),
(6, 'Waiting', 'The task is waiting for a resource'),
(7, 'Done', 'The task has been completed');
COMMIT;
-- -----------------------------------------------------
-- Insert tasks
-- -----------------------------------------------------
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('NS10','1');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('DO02','7');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('DO03','7');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('DO04','7');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('DO05','7');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('DO06','7');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('DO07','7');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('DO08','7');

COMMIT;
-- -----------------------------------------------------
-- Insert tasklimit
-- -----------------------------------------------------
INSERT INTO `kanbanIT`.`tasklimit` (`maximumTask`) VALUES ('10');
COMMIT;
