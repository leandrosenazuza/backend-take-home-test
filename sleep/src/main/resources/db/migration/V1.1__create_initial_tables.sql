-- First version of the tables. The id of both tables will use UUID.

CREATE TABLE tab_user (
                          id_user VARCHAR(36) PRIMARY KEY,
                          val_user_name VARCHAR(255) NOT NULL,
                          dat_create TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE tab_sleep_log (
                               id_sleep VARCHAR(36) PRIMARY KEY,
                               id_user VARCHAR(36) NOT NULL,
                               dat_sleep_date TIMESTAMP WITH TIME ZONE NOT NULL,
                               dat_bed_time_start TIMESTAMP WITH TIME ZONE NOT NULL,
                               dat_bed_time_end TIMESTAMP WITH TIME ZONE NOT NULL,
                               val_total_time_bed_minutes DOUBLE PRECISION,
                               ind_feeling_morning VARCHAR(255) NOT NULL,
                               dat_create TIMESTAMP WITH TIME ZONE NOT NULL,
                               FOREIGN KEY (id_user) REFERENCES tab_user(id_user)
);

CREATE INDEX idx_sleep_log_user_id ON tab_sleep_log(id_user);