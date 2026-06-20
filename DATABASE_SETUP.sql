-- Create database
CREATE DATABASE IF NOT EXISTS batch_db;
USE batch_db;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    country VARCHAR(100),
    user_id VARCHAR(50) NOT NULL UNIQUE,
    created_at DATE,
    UNIQUE KEY unique_user_id (user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_last_name (last_name),
    INDEX idx_first_name (first_name)
);

-- Create batch_job_instance table (for Spring Batch meta-data)
CREATE TABLE IF NOT EXISTS batch_job_instance (
    job_instance_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version BIGINT,
    job_name VARCHAR(100) NOT NULL,
    job_key VARCHAR(32) NOT NULL,
    UNIQUE KEY job_inst_un (job_name, job_key)
) ENGINE=InnoDB;

-- Create batch_job_execution table (for Spring Batch meta-data)
CREATE TABLE IF NOT EXISTS batch_job_execution (
    job_execution_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version BIGINT,
    job_instance_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL,
    start_time DATETIME DEFAULT NULL,
    end_time DATETIME DEFAULT NULL,
    status VARCHAR(10),
    exit_code VARCHAR(20),
    exit_message VARCHAR(2500),
    last_updated DATETIME,
    FOREIGN KEY (job_instance_id) REFERENCES batch_job_instance(job_instance_id)
) ENGINE=InnoDB;

-- Create batch_job_execution_params table (for Spring Batch meta-data)
CREATE TABLE IF NOT EXISTS batch_job_execution_params (
    job_execution_id BIGINT NOT NULL,
    type_cd VARCHAR(6) NOT NULL,
    key_name VARCHAR(100) NOT NULL,
    string_val VARCHAR(250),
    date_val DATETIME DEFAULT NULL,
    long_val BIGINT,
    double_val DOUBLE PRECISION,
    identifying CHAR(1),
    FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id)
) ENGINE=InnoDB;

-- Create batch_step_execution table (for Spring Batch meta-data)
CREATE TABLE IF NOT EXISTS batch_step_execution (
    step_execution_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version BIGINT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    job_execution_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL,
    start_time DATETIME DEFAULT NULL,
    end_time DATETIME DEFAULT NULL,
    status VARCHAR(10),
    commit_count BIGINT,
    read_count BIGINT,
    filter_count BIGINT,
    write_count BIGINT,
    read_skip_count BIGINT,
    write_skip_count BIGINT,
    process_skip_count BIGINT,
    rollback_count BIGINT,
    exit_code VARCHAR(20),
    exit_message VARCHAR(2500),
    last_updated DATETIME,
    FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id)
) ENGINE=InnoDB;

-- Create batch_step_execution_context table (for Spring Batch meta-data)
CREATE TABLE IF NOT EXISTS batch_step_execution_context (
    step_execution_id BIGINT PRIMARY KEY,
    short_context VARCHAR(2500) NOT NULL,
    serialized_context TEXT,
    FOREIGN KEY (step_execution_id) REFERENCES batch_step_execution(step_execution_id)
) ENGINE=InnoDB;

-- Create batch_job_execution_context table (for Spring Batch meta-data)
CREATE TABLE IF NOT EXISTS batch_job_execution_context (
    job_execution_id BIGINT PRIMARY KEY,
    short_context VARCHAR(2500) NOT NULL,
    serialized_context TEXT,
    FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id)
) ENGINE=InnoDB;

COMMIT;
