-- Create tables if they don't exist
CREATE TABLE IF NOT EXISTS user_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN DEFAULT TRUE,
    student_id BIGINT UNIQUE,
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    credit_hours INT NOT NULL,
    fee DOUBLE NOT NULL DEFAULT 1000.0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS enrollments (
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Insert initial admin user (password: admin123)
MERGE INTO user_accounts (username, password, email, active)
KEY (username)
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'admin@example.com', TRUE);

MERGE INTO user_roles (user_id, role)
KEY (user_id, role)
VALUES ((SELECT id FROM user_accounts WHERE username = 'admin'), 'ROLE_ADMIN');

-- Insert sample courses
MERGE INTO courses (code, title, description, credit_hours, fee, active)
KEY (code)
VALUES
('CS101', 'Introduction to Computer Science', 'Basic concepts of computer science and programming', 3, 1200.0, TRUE),
('MATH201', 'Calculus I', 'Introduction to differential and integral calculus', 4, 1100.0, TRUE),
('PHYS101', 'Physics I', 'Mechanics, heat, and waves', 4, 1150.0, TRUE),
('ENG101', 'English Composition', 'College-level writing and composition', 3, 900.0, TRUE),
('HIST101', 'World History', 'Survey of world history from ancient times to present', 3, 950.0, TRUE);

-- Insert a sample student (password: student123)
MERGE INTO students (student_id, first_name, last_name)
KEY (student_id)
VALUES ('S1001', 'John', 'Doe');

MERGE INTO user_accounts (username, password, email, active, student_id)
KEY (username)
VALUES ('student1', '$2a$10$IqTJTjn39IU5.7sSCDQxzu3xug6z/LPU6IF0azE/8CkHCwYEnwBX.', 'student1@example.com', TRUE, (SELECT id FROM students WHERE student_id = 'S1001'));

MERGE INTO user_roles (user_id, role)
KEY (user_id, role)
VALUES ((SELECT id FROM user_accounts WHERE username = 'student1'), 'ROLE_STUDENT');
