-- Status

-- INSERT INTO kanbanIT.statustasks (statusid, statusname, statusdescription) VALUES
-- (1, 'NO_STATUS', NULL),
-- (2, 'TO_DO', NULL),
-- (3, 'DOING', NULL),
-- (4, 'DONE', NULL);

INSERT INTO kanbanIT.statustasks (statusid, statusname, statusdescription) VALUES
(1, 'No Status', 'The default status'),
(2, 'To Do', NULL),
(3, 'Doing', 'Being worked on'),
(4, 'Done', 'Finished');

COMMIT;
