-- Create bot_users table
CREATE TABLE bot_users (
    id UUID NOT NULL,
    chat_id BIGINT NOT NULL,
    username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    language_code VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_bot_users PRIMARY KEY (id)
);

-- Create unique index on chat_id
CREATE UNIQUE INDEX ux_bot_users_chat_id ON bot_users (chat_id);
