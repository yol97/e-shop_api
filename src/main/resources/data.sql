INSERT INTO product (name, description, image_url, is_active, price, stock, discount, created_at, updated_at)
VALUES
(
  'Clavier mécanique',
  'Clavier mécanique rétroéclairé RGB avec switches bleus',
  'https://example.com/images/clavier.jpg',
  true,
  79.99,
  12,
  0.0,
  NOW(),
  NOW()
),
(
  'Souris gamer',
  'Souris optique ergonomique 16000 DPI avec rétroéclairage personnalisable',
  'https://example.com/images/souris.jpg',
  true,
  59.99,
  25,
  0.0,
  NOW(),
  NOW()
),
(
  'Casque audio',
  'Casque circum-aural avec micro amovible et son surround 7.1',
  'https://example.com/images/casque.jpg',
  true,
  99.99,
  8,
  0.0,
  NOW(),
  NOW()
),
(
  'Écran 27 pouces',
  'Écran gaming 27 pouces QHD 165Hz avec technologie G-Sync',
  'https://example.com/images/ecran.jpg',
  true,
  199.99,
  5,
  0.0,
  NOW(),
  NOW()
);