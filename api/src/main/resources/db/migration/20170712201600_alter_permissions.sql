-- Insert "session_view" and "session_modify" into Permissions
INSERT INTO permissions(name, created_at, created_by)
    VALUES ('session_view', now(), 'system');

INSERT INTO permissions(name, created_at, created_by)
    VALUES ('session_modify', now(), 'system');
