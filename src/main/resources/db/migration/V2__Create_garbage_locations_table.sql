-- Create garbage_locations table
CREATE TABLE garbage_locations (
    id UUID NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_garbage_locations PRIMARY KEY (id)
);
