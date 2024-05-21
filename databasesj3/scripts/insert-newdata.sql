-- new 7 insert
-- Status
INSERT INTO kanbanIT.statustasks (statusid, statusname, statusdescription) VALUES
(1, 'No Status', 'A status has not been assigned'),
(2, 'To Do', 'The task is included in the project'),
(3, 'In Progress', 'The task is being worked on'),
(4, 'Reviewing', 'The task is being reviewed'),
(5, 'Testing', 'The task is being tested'),
(6, 'Waiting', 'The task is waiting for a resource'),
(7, 'Done', 'The task has been completed');
COMMIT;

-- Tasks
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`, `statusId`) VALUES ('NS01','1');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('TD01','2');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('IP01','3');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('TD02','2');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('DO01','7');
INSERT INTO `kanbanIT`.`tasks` (`taskTitle`,`statusId`) VALUES ('IP02','3');
COMMIT;