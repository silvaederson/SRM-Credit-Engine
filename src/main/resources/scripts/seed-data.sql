-- Moedas adicionais
INSERT INTO currencies (code, name, is_active) VALUES
('CHF', 'Swiss Franc', true),
('CAD', 'Canadian Dollar', true),
('AUD', 'Australian Dollar', true),
('CNY', 'Chinese Yuan', true),
('ARS', 'Argentine Peso', true)
ON CONFLICT (code) DO NOTHING;

-- Mais tipos de recebíveis
INSERT INTO receivable_types (name, spread, category, is_active) VALUES
('Export Invoice', 0.018, 'International', true),
('Import Invoice', 0.022, 'International', true),
('Real Estate Receivable', 0.028, 'Real Estate', true),
('Agricultural Receivable', 0.032, 'Agriculture', true),
('Technology Receivable', 0.025, 'Technology', true)
ON CONFLICT (name) DO NOTHING;

-- Mais cedentes
INSERT INTO creditors (name, document, email, phone, is_active) VALUES
('Tech Solutions Inc', '12345678000199', 'finance@techsolutions.com', '(11) 93456-7890', true),
('Agro Brasil LTDA', '98765432000188', 'contato@agrobrasil.com', '(11) 93456-7891', true),
('Global Trade Corp', '45678912000177', 'finance@globaltrade.com', '(11) 93456-7892', true)
ON CONFLICT (document) DO NOTHING;

-- Taxas de câmbio iniciais
INSERT INTO exchange_rates (from_currency, to_currency, rate, effective_date) VALUES
('USD', 'BRL', 5.2345, CURRENT_DATE),
('EUR', 'BRL', 6.0123, CURRENT_DATE),
('GBP', 'BRL', 7.2345, CURRENT_DATE),
('CHF', 'BRL', 5.8900, CURRENT_DATE),
('CAD', 'BRL', 3.8900, CURRENT_DATE),
('AUD', 'BRL', 3.4500, CURRENT_DATE),
('CNY', 'BRL', 0.7200, CURRENT_DATE),
('ARS', 'BRL', 0.0060, CURRENT_DATE),
('USD', 'EUR', 0.8710, CURRENT_DATE),
('USD', 'GBP', 0.7920, CURRENT_DATE)
ON CONFLICT (from_currency, to_currency, effective_date) DO NOTHING;