-- roles
MERGE INTO roles (name) KEY(name) VALUES ('ADMIN');
MERGE INTO roles (name) KEY(name) VALUES ('TECH_PANEL');
MERGE INTO roles (name) KEY(name) VALUES ('HR_PANEL');

MERGE INTO users (email, full_name, password, role_id, is_active)
KEY(email)
VALUES (
  'admin@example.com',
  'System Admin',
  '$2a$12$3L1gVSFIwVhCaDtF28UZUuHFP0Rodew3XnQwGLWYxnijVh5JmJ/cG', -- bcrypt of 'admin123' (example)
  (SELECT id FROM roles WHERE name='ADMIN'),
  TRUE
);