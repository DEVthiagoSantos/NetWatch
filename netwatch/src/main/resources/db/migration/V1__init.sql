CREATE TABlE device (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
   ip_address VARCHAR(45) NOT NULL UNIQUE,
   type VARCHAR(20) NOT NULL,
   active BOOLEAN NOT NULL DEFAULT TRUE,
   current_status VARCHAR(10) NOT NULL DEFAULT 'UNKNOWN',
   last_latency_ms DOUBLE PRECISION,
   last_checked_at TIMESTAMPTZ,
   created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE check_result (
   id BIGSERIAL PRIMARY KEY,
   device_id BIGINT NOT NULL REFERENCES device(id) ON DELETE CASCADE,
   status VARCHAR(10) NOT NULL,
   latency_ms DOUBLE PRECISION,
   message VARCHAR(255),
   checked_at TIMESTAMPTZ NOT NULL
);

CREATe INDEX idx_check_result_device_time ON check_result (device_id, checked_at DESC);