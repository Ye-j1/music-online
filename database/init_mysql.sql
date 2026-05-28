-- ==============================================
-- MusicOnline Database Initialisation Script
-- MySQL 8.0
-- ==============================================

CREATE DATABASE IF NOT EXISTS music_online
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE music_online;

-- ── Users ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS mo_users (
  id            BIGINT        NOT NULL AUTO_INCREMENT,
  email         VARCHAR(255)  NOT NULL,
  password_hash VARCHAR(255)  NOT NULL,
  username      VARCHAR(100)  NOT NULL,
  role          VARCHAR(20)   NOT NULL DEFAULT 'USER',
  enabled       TINYINT(1)    NOT NULL DEFAULT 1,
  created_at    DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_email    (email),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Vinyls ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS mo_vinyls (
  id           BIGINT         NOT NULL AUTO_INCREMENT,
  title        VARCHAR(200)   NOT NULL,
  artist       VARCHAR(200)   NOT NULL,
  type         VARCHAR(10)    NOT NULL,
  genre        VARCHAR(100),
  label        VARCHAR(100),
  release_date DATE,
  price        DECIMAL(10,2)  NOT NULL,
  `condition`  VARCHAR(50),
  description  TEXT,
  image_url    VARCHAR(500),
  available    TINYINT(1)     NOT NULL DEFAULT 1,
  seller_id    BIGINT         NOT NULL,
  created_at   DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at   DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_artist (artist),
  KEY idx_title  (title),
  KEY idx_type   (type),
  KEY idx_seller (seller_id),
  CONSTRAINT fk_vinyl_seller FOREIGN KEY (seller_id) REFERENCES mo_users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Audit Log ──────────────────────────────────
CREATE TABLE IF NOT EXISTS mo_audit_log (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  actor        VARCHAR(255),
  actor_role   VARCHAR(20),
  action       VARCHAR(40)  NOT NULL,
  entity_type  VARCHAR(50),
  entity_id    VARCHAR(50),
  detail       VARCHAR(500),
  ip_address   VARCHAR(50),
  occurred_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_actor  (actor),
  KEY idx_action (action),
  KEY idx_time   (occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ==============================================
-- SEED DATA
-- All demo accounts use BCrypt-hashed "password"
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ==============================================

SET @pw = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

-- Admin accounts (minimum 2 required)
INSERT INTO mo_users (email, password_hash, username, role) VALUES
  ('admin@musiconline.com',  @pw, 'admin1',      'ADMIN'),
  ('admin2@musiconline.com', @pw, 'admin2',       'ADMIN'),
  ('alice@example.com',      @pw, 'alice_vinyl',  'USER'),
  ('bob@example.com',        @pw, 'bob_records',  'RETAILER'),
  ('carol@example.com',      @pw, 'carol_music',  'USER'),
  ('dave@example.com',       @pw, 'dave_store',   'RETAILER');

-- Vinyl records (minimum 15 required)
-- Albums (10)
INSERT INTO mo_vinyls (title, artist, type, genre, label, release_date, price, `condition`, seller_id) VALUES
  ('Abbey Road',             'The Beatles',       'ALBUM', 'Rock',         'Apple Records',  '1969-09-26', 24.99, 'Near Mint', 3),
  ('Rumours',                'Fleetwood Mac',     'ALBUM', 'Rock',         'Warner Bros.',   '1977-02-04', 19.99, 'Very Good', 3),
  ('Thriller',               'Michael Jackson',   'ALBUM', 'Pop',          'Epic',           '1982-11-30', 29.99, 'Mint',      4),
  ('Dark Side of the Moon',  'Pink Floyd',        'ALBUM', 'Progressive',  'Harvest',        '1973-03-01', 34.99, 'Very Good', 4),
  ('Purple Rain',            'Prince',            'ALBUM', 'Pop',          'Warner Bros.',   '1984-06-25', 22.50, 'Near Mint', 5),
  ('Kind of Blue',           'Miles Davis',       'ALBUM', 'Jazz',         'Columbia',       '1959-08-17', 39.99, 'Good',      5),
  ('Born to Run',            'Bruce Springsteen', 'ALBUM', 'Rock',         'Columbia',       '1975-08-25', 18.99, 'Very Good', 6),
  ('Nevermind',              'Nirvana',           'ALBUM', 'Grunge',       'DGC Records',    '1991-09-24', 21.99, 'Near Mint', 6),
  ('Songs in the Key of Life','Stevie Wonder',    'ALBUM', 'Soul',         'Tamla',          '1976-09-28', 27.99, 'Very Good', 3),
  ('Achtung Baby',           'U2',                'ALBUM', 'Rock',         'Island Records', '1991-11-18', 16.99, 'Good',      4);

-- EPs (3)
INSERT INTO mo_vinyls (title, artist, type, genre, label, release_date, price, `condition`, seller_id) VALUES
  ('All I Want for Christmas Is You', 'Mariah Carey', 'EP', 'Pop',        'Columbia', '1994-11-01', 12.99, 'Mint',      5),
  ('Twist and Shout',                 'The Beatles',  'EP', 'Rock',       'Parlophone','1963-07-12', 14.99, 'Good',      6),
  ('Cashflow',                        'Tame Impala',  'EP', 'Psychedelic','Modular',  '2012-05-04', 11.99, 'Near Mint', 3);

-- Singles (5)
INSERT INTO mo_vinyls (title, artist, type, genre, label, release_date, price, `condition`, seller_id) VALUES
  ('Bohemian Rhapsody',      'Queen',          'SINGLE', 'Rock',        'EMI',         '1975-10-31',  9.99, 'Very Good', 4),
  ('Johnny B. Goode',        'Chuck Berry',    'SINGLE', 'Rock and Roll','Chess Records','1958-03-31', 8.50, 'Good',      5),
  ('Respect',                'Aretha Franklin','SINGLE', 'Soul',        'Atlantic',    '1967-04-14', 10.99, 'Very Good', 6),
  ('Smells Like Teen Spirit', 'Nirvana',       'SINGLE', 'Grunge',      'DGC Records', '1991-09-10',  7.99, 'Near Mint', 3),
  ('Like a Rolling Stone',   'Bob Dylan',      'SINGLE', 'Folk Rock',   'Columbia',    '1965-07-20', 11.50, 'Good',      4);

-- Audit: seed events
INSERT INTO mo_audit_log (actor, actor_role, action, entity_type, entity_id, detail, ip_address) VALUES
  ('admin@musiconline.com',  'ADMIN',    'REGISTER', 'User', '1', 'New account: ADMIN',    '127.0.0.1'),
  ('admin2@musiconline.com', 'ADMIN',    'REGISTER', 'User', '2', 'New account: ADMIN',    '127.0.0.1'),
  ('alice@example.com',      'USER',     'REGISTER', 'User', '3', 'New account: USER',     '127.0.0.1'),
  ('bob@example.com',        'RETAILER', 'REGISTER', 'User', '4', 'New account: RETAILER', '127.0.0.1');
