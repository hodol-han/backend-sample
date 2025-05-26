-- Add created_at and updated_at columns to the users table for MySQL
ALTER TABLE users
ADD COLUMN created_at DATETIME(6), -- MySQL DATETIME(6) for microsecond precision
ADD COLUMN updated_at DATETIME(6); -- MySQL DATETIME(6) for microsecond precision

-- Update existing rows to set a default value for the new columns.
-- It's important to populate these for existing records before adding NOT NULL constraints.
-- For MySQL, CURRENT_TIMESTAMP or NOW() can be used. NOW(6) for microsecond precision.
UPDATE users SET created_at = NOW(6), updated_at = NOW(6) WHERE created_at IS NULL AND updated_at IS NULL;

-- Add NOT NULL constraints to the new columns for MySQL
ALTER TABLE users
  MODIFY COLUMN created_at DATETIME(6) NOT NULL,
  MODIFY COLUMN updated_at DATETIME(6) NOT NULL;
