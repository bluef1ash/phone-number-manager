INSERT INTO pm_system_user (id, username, password, login_time,
                            is_locked, is_enabled, account_expire_time, credential_expire_time,
                            create_time, update_time)
VALUES (1, 'admin', '$2a$10$egVm6KKVXF1Tsstk/XJzN.bczjhVPJ9q0.iaaPaBVjNPDWDYv0px6', '1000-01-01 00:00:00', 0, 1,
        '9999-12-31 23:59:59', '9999-12-31 23:59:59', '1000-01-01 00:00:00', '1000-01-01 00:00:00');
