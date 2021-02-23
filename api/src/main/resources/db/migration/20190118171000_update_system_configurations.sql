-- add 'Terminal Control' to range of system configuration item 'Approach fees label'
UPDATE system_configurations
    SET range = 'Approach,Landing,ADAP,Terminal Control'
    WHERE item_name = 'Approach fees label';
