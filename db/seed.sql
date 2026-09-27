-- Demo data for local runs. Sign in with demo@timetotrack.dev / demo1234.
-- Loaded by docker-compose after schema.sql, only when the database volume is first created.

INSERT INTO "user" (username, email, full_name, password_hash) VALUES
    ('demo',  'demo@timetotrack.dev',  'Demo User',       '$pbkdf2$t8rvvA7KTyFi2U3s+lgZYQ==$B2bJ1iE/MNOLrlVfL3b4kLBBvEhVKrnFgHDVu8IgePeOwWW0wA04/3fPgsOyt72MnygSCMFnA8JB17Y4rRUIWw'),
    ('laura', 'laura@timetotrack.dev', 'Laura Fernández', '$pbkdf2$t8rvvA7KTyFi2U3s+lgZYQ==$B2bJ1iE/MNOLrlVfL3b4kLBBvEhVKrnFgHDVu8IgePeOwWW0wA04/3fPgsOyt72MnygSCMFnA8JB17Y4rRUIWw');

INSERT INTO customer (customer_name) VALUES ('Acme Corp'), ('Globex');

INSERT INTO projects (project_name, customer_id) VALUES
    ('Website Redesign', 1),
    ('Mobile App', 1),
    ('Data Platform', 2);

-- Six past days of work for the demo user, with lengths varying by day so the chart has shape.
INSERT INTO time_entry (user_id, project_id, description, from_time, to_time)
SELECT 1,
       p.project_id,
       p.description,
       date_trunc('day', now()) - make_interval(days => d.days_ago) + p.start_at,
       date_trunc('day', now()) - make_interval(days => d.days_ago) + p.start_at + p.length * (1 + (d.days_ago % 3) * 0.25)
FROM generate_series(1, 6) AS d(days_ago)
CROSS JOIN (VALUES
    (1, 'Landing page layout',       interval '9 hours',  interval '2 hours 30 minutes'),
    (2, 'Push notification flow',    interval '13 hours', interval '1 hour 45 minutes'),
    (3, 'Ingestion pipeline review', interval '16 hours', interval '1 hour 15 minutes')
) AS p(project_id, description, start_at, length);
