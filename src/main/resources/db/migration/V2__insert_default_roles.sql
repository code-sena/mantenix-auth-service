-- V2: the three Mantenix roles (security-policy.md, auth-service data-model.md)

INSERT INTO roles (name, description) VALUES
    ('ADMIN',    'Platform administrator'),
    ('USER',     'Vehicle owner: manages their own vehicles, maintenance history and alerts'),
    ('MECHANIC', 'Repair shop / mechanic: records services performed on vehicles');
