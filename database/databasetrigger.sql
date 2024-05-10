DELIMITER //

CREATE TRIGGER before_statustasks_update
BEFORE UPDATE ON kanbanIT.statustasks
FOR EACH ROW
BEGIN
    IF OLD.statusid != NEW.statusid THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot update statusid in statustasks table';
    END IF;
END;
//

CREATE TRIGGER before_statustasks_delete
BEFORE DELETE ON kanbanIT.statustasks
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Cannot delete rows from statustasks table';
END;
//

DELIMITER ;
