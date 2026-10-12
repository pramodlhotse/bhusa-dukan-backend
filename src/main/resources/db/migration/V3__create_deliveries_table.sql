CREATE TABLE deliveries (
                            id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

                            order_id BIGINT UNSIGNED NOT NULL,

                            delivery_person_id BIGINT UNSIGNED NULL,

                            vehicle_number VARCHAR(50) NULL,

                            status ENUM(
        'ASSIGNED',
        'OUT_FOR_DELIVERY',
        'DELIVERED',
        'FAILED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'ASSIGNED',

                            assigned_at DATETIME NULL,

                            dispatched_at DATETIME NULL,

                            delivered_at DATETIME NULL,

                            failed_at DATETIME NULL,

                            failure_reason VARCHAR(500) NULL,

                            delivery_otp VARCHAR(10) NULL,

                            customer_confirmed BOOLEAN NOT NULL DEFAULT FALSE,

                            remarks VARCHAR(500) NULL,

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP,

                            PRIMARY KEY (id),

                            CONSTRAINT fk_delivery_order
                                FOREIGN KEY (order_id)
                                    REFERENCES orders(id),

                            CONSTRAINT fk_delivery_person
                                FOREIGN KEY (delivery_person_id)
                                    REFERENCES users(id),

                            UNIQUE KEY uk_delivery_order (order_id),

                            INDEX idx_delivery_status (status),

                            INDEX idx_delivery_person (delivery_person_id),

                            INDEX idx_delivery_assigned_at (assigned_at)

) ENGINE=InnoDB;