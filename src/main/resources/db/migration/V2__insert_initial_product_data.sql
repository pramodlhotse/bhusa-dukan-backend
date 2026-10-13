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