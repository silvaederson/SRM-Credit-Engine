-- Inserir moedas
INSERT INTO currencies (code, name, is_active) VALUES
('BRL', 'Brazilian Real', TRUE),
('USD', 'US Dollar', TRUE),
('EUR', 'Euro', TRUE),
('GBP', 'British Pound', TRUE),
('JPY', 'Japanese Yen', TRUE);

-- Inserir tipos de recebíveis (sem a coluna category)
INSERT INTO receivable_types (name, spread, is_active) VALUES
('Commercial Invoice', 0.015, TRUE),
('Post-Dated Check', 0.025, TRUE),
('Government Bond', 0.008, TRUE),
('Corporate Bond', 0.012, TRUE),
('Consumer Loan', 0.035, TRUE);

-- Inserir cedentes de exemplo
INSERT INTO creditors (name, document, email, phone, is_active) VALUES
('ABC Corporation', '12345678901234', 'finance@abccorp.com', '(11) 91234-5678', TRUE),
('XYZ Industries', '98765432109876', 'contato@xyz.com', '(11) 98765-4321', TRUE),
('Brazilian Government', '00123456789', 'tesouro@fazenda.gov.br', '(61) 91234-5678', TRUE);