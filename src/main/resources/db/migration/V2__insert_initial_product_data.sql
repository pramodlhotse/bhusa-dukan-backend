USE bhusa_dukan;

-- =========================================================
-- PRODUCT CATEGORIES
-- =========================================================

INSERT INTO product_categories
(name, description, active)
VALUES
    ('Bhusa', 'Animal feed and agricultural bhusa products', TRUE),
    ('Other Feed', 'Other animal feed products', TRUE);


-- =========================================================
-- PRODUCTS
-- =========================================================

INSERT INTO products
(category_id, name, description, unit, minimum_quantity, active)
VALUES
    (
        (SELECT id FROM product_categories WHERE name = 'Bhusa'),
        'Wheat Bhusa',
        'Good quality wheat bhusa',
        'KG',
        25.000,
        TRUE
    ),
    (
        (SELECT id FROM product_categories WHERE name = 'Bhusa'),
        'Paddy Bhusa',
        'Good quality paddy bhusa',
        'KG',
        25.000,
        TRUE
    );


-- =========================================================
-- PRODUCT PRICES
-- =========================================================

INSERT INTO product_prices
(product_id, price, effective_from, active)
VALUES
    (
        (SELECT id FROM products WHERE name = 'Wheat Bhusa'),
        8.50,
        NOW(),
        TRUE
    ),
    (
        (SELECT id FROM products WHERE name = 'Paddy Bhusa'),
        7.50,
        NOW(),
        TRUE
    );


-- =========================================================
-- INVENTORY
-- =========================================================

INSERT INTO inventory
(product_id, current_quantity, reserved_quantity, reorder_level)
VALUES
    (
        (SELECT id FROM products WHERE name = 'Wheat Bhusa'),
        5000.000,
        0.000,
        500.000
    ),
    (
        (SELECT id FROM products WHERE name = 'Paddy Bhusa'),
        3000.000,
        0.000,
        300.000
    );