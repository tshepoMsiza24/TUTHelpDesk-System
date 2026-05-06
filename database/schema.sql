-- TUTHelpDesk Database Schema

-- Drop tables if they exist
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS tickets CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table (Students and Admins)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tickets table
CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    category_id BIGINT REFERENCES categories(id),
    student_id BIGINT NOT NULL REFERENCES users(id),
    assigned_to BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

-- Comments table
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default categories
INSERT INTO categories (name, description) VALUES
('Technical Support', 'Hardware and software issues'),
('Account Issues', 'Login, password, and account-related problems'),
('Course Registration', 'Issues with course enrollment and registration'),
('Financial Aid', 'Questions about fees, payments, and financial aid'),
('General Inquiry', 'General questions and other issues');

-- Insert default admin user (password: admin123)
-- Note: In production, use properly hashed passwords
INSERT INTO users (username, email, password, full_name, role) VALUES
('admin', 'admin@tut.ac.za', '$2a$10$xqxQ8Z8Z8Z8Z8Z8Z8Z8Z8uKJ5YvXqxQ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z', 'System Administrator', 'ADMIN');

-- Insert sample student user (password: student123)
INSERT INTO users (username, email, password, full_name, role) VALUES
('student1', 'student1@tut.ac.za', '$2a$10$yqyQ8Z8Z8Z8Z8Z8Z8Z8Z8uKJ5YvXqxQ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z', 'John Doe', 'STUDENT');

-- Create indexes for better performance
CREATE INDEX idx_tickets_student_id ON tickets(student_id);
CREATE INDEX idx_tickets_assigned_to ON tickets(assigned_to);
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_tickets_category_id ON tickets(category_id);
CREATE INDEX idx_comments_ticket_id ON comments(ticket_id);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

