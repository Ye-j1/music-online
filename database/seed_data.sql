-- ================================================================
-- MusicOnline — Full Database Schema + Seed Data
-- PDF Task 2: Separate tables for admin, registered users, music
-- ================================================================

USE music_online;

-- ── Drop order respects FK constraints ──────────────────────────
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS mo_audit_log;
DROP TABLE IF EXISTS mo_orders;
DROP TABLE IF EXISTS mo_vinyls;
DROP TABLE IF EXISTS mo_admins;
DROP TABLE IF EXISTS mo_users;
SET FOREIGN_KEY_CHECKS = 1;

-- ================================================================
-- Table 1: mo_admins  (PDF Task 2 — dedicated admin table)
-- ================================================================
CREATE TABLE mo_admins (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    email         VARCHAR(255)   NOT NULL,
    username      VARCHAR(100)   NOT NULL,
    password_hash VARCHAR(255)   NOT NULL,
    admin_level   VARCHAR(30)    NOT NULL DEFAULT 'MODERATOR',
    active        TINYINT(1)     NOT NULL DEFAULT 1,
    created_at    DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    last_login_at DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_admins_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Dedicated admin accounts (PDF Task 2)';

-- ================================================================
-- Table 2: mo_users  (registered users — buyers, retailers)
-- ================================================================
CREATE TABLE mo_users (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    email         VARCHAR(255)   NOT NULL,
    password_hash VARCHAR(255)   NOT NULL,
    username      VARCHAR(100)   NOT NULL,
    role          VARCHAR(20)    NOT NULL DEFAULT 'USER',
    enabled       TINYINT(1)     NOT NULL DEFAULT 1,
    created_at    DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Registered user accounts (buyers and retailers)';

-- ================================================================
-- Table 3: mo_vinyls  (music / vinyl listings)
-- ================================================================
CREATE TABLE mo_vinyls (
    id           BIGINT          NOT NULL AUTO_INCREMENT,
    title        VARCHAR(200)    NOT NULL,
    artist       VARCHAR(200)    NOT NULL,
    type         VARCHAR(10)     NOT NULL COMMENT 'ALBUM | EP | SINGLE',
    genre        VARCHAR(100)    DEFAULT NULL,
    label        VARCHAR(100)    DEFAULT NULL,
    release_date DATE            DEFAULT NULL,
    price        DECIMAL(10,2)   NOT NULL,
    `condition`  VARCHAR(50)     DEFAULT NULL COMMENT 'Mint | Near Mint | Very Good | Good | Fair',
    description  VARCHAR(2000)   DEFAULT NULL,
    image_url    VARCHAR(500)    DEFAULT NULL,
    available    TINYINT(1)      NOT NULL DEFAULT 1,
    seller_id    BIGINT          NOT NULL,
    created_at   DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at   DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_vinyls_seller (seller_id),
    KEY idx_vinyls_type (type),
    CONSTRAINT fk_vinyls_seller FOREIGN KEY (seller_id) REFERENCES mo_users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Vinyl record listings (albums, EPs, singles)';

-- ================================================================
-- Table 4: mo_audit_log  (security audit trail)
-- ================================================================
CREATE TABLE mo_audit_log (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    actor       VARCHAR(255) DEFAULT NULL,
    actor_role  VARCHAR(20)  DEFAULT NULL,
    action      VARCHAR(40)  NOT NULL,
    entity_type VARCHAR(50)  DEFAULT NULL,
    entity_id   VARCHAR(50)  DEFAULT NULL,
    detail      VARCHAR(500) DEFAULT NULL,
    ip_address  VARCHAR(50)  DEFAULT NULL,
    occurred_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Security and activity audit log';

-- ================================================================
-- Table 5: mo_orders  (purchase orders)
-- ================================================================
CREATE TABLE mo_orders (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    buyer_id    BIGINT        NOT NULL,
    vinyl_id    BIGINT        NOT NULL,
    price_paid  DECIMAL(10,2) NOT NULL,
    ordered_at  DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_orders_buyer (buyer_id),
    KEY idx_orders_vinyl (vinyl_id),
    CONSTRAINT fk_orders_buyer FOREIGN KEY (buyer_id) REFERENCES mo_users (id),
    CONSTRAINT fk_orders_vinyl FOREIGN KEY (vinyl_id) REFERENCES mo_vinyls (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Purchase orders for vinyl records';

-- ================================================================
-- Seed Data
-- ================================================================

-- BCrypt hash of 'password' (cost 10, verified with bcrypt)
SET @pw = '$2b$10$6.AazyhafQEr1w06PCgPueZJnaw02qQHusZpdm8B7n1EQJV9ZfjjW';

-- ── Admin accounts (mo_admins) ───────────────────────────────────
INSERT INTO mo_admins (email, username, password_hash, admin_level, active) VALUES
  ('admin@musiconline.com',  'admin1', @pw, 'SUPER',     1),
  ('admin2@musiconline.com', 'admin2', @pw, 'MODERATOR', 1);

-- ── User accounts (mo_users) ─────────────────────────────────────
-- Admin users are ALSO in mo_users so Spring Security can authenticate them
INSERT INTO mo_users (email, password_hash, username, role, enabled) VALUES
  ('admin@musiconline.com',  @pw, 'admin1',      'ADMIN',    1),
  ('admin2@musiconline.com', @pw, 'admin2',      'ADMIN',    1),
  ('alice@example.com',      @pw, 'alice_vinyl', 'USER',     1),
  ('bob@example.com',        @pw, 'bob_records', 'RETAILER', 1),
  ('carol@example.com',      @pw, 'carol_music', 'USER',     1),
  ('dave@example.com',       @pw, 'dave_store',  'RETAILER', 1);

-- ── Vinyl records (mo_vinyls) ────────────────────────────────────
-- Albums (10)
INSERT INTO mo_vinyls (title, artist, type, genre, label, release_date, price, `condition`, seller_id) VALUES
  ('Abbey Road',               'The Beatles',       'ALBUM', 'Rock',         'Apple Records',  '1969-09-26', 24.99, 'Near Mint', 3),
  ('Rumours',                  'Fleetwood Mac',     'ALBUM', 'Rock',         'Warner Bros.',   '1977-02-04', 19.99, 'Very Good', 3),
  ('Thriller',                 'Michael Jackson',   'ALBUM', 'Pop',          'Epic',           '1982-11-30', 29.99, 'Mint',      4),
  ('Dark Side of the Moon',    'Pink Floyd',        'ALBUM', 'Progressive',  'Harvest',        '1973-03-01', 34.99, 'Very Good', 4),
  ('Purple Rain',              'Prince',            'ALBUM', 'Pop',          'Warner Bros.',   '1984-06-25', 22.50, 'Near Mint', 5),
  ('Kind of Blue',             'Miles Davis',       'ALBUM', 'Jazz',         'Columbia',       '1959-08-17', 39.99, 'Good',      5),
  ('Born to Run',              'Bruce Springsteen', 'ALBUM', 'Rock',         'Columbia',       '1975-08-25', 18.99, 'Very Good', 6),
  ('Nevermind',                'Nirvana',           'ALBUM', 'Grunge',       'DGC Records',    '1991-09-24', 21.99, 'Near Mint', 6),
  ('Songs in the Key of Life', 'Stevie Wonder',     'ALBUM', 'Soul',         'Tamla',          '1976-09-28', 27.99, 'Very Good', 3),
  ('Achtung Baby',             'U2',                'ALBUM', 'Rock',         'Island Records', '1991-11-18', 16.99, 'Good',      4);

-- EPs (3)
INSERT INTO mo_vinyls (title, artist, type, genre, label, release_date, price, `condition`, seller_id) VALUES
  ('All I Want for Christmas Is You', 'Mariah Carey', 'EP', 'Pop',         'Columbia',   '1994-11-01', 12.99, 'Mint',      5),
  ('Twist and Shout',                 'The Beatles',  'EP', 'Rock',        'Parlophone', '1963-07-12', 14.99, 'Good',      6),
  ('Cashflow',                        'Tame Impala',  'EP', 'Psychedelic', 'Modular',    '2012-05-04', 11.99, 'Near Mint', 3);

-- Singles (5)
INSERT INTO mo_vinyls (title, artist, type, genre, label, release_date, price, `condition`, seller_id) VALUES
  ('Bohemian Rhapsody',       'Queen',           'SINGLE', 'Rock',          'EMI',           '1975-10-31',  9.99, 'Very Good', 4),
  ('Johnny B. Goode',         'Chuck Berry',     'SINGLE', 'Rock and Roll', 'Chess Records', '1958-03-31',  8.50, 'Good',      5),
  ('Respect',                 'Aretha Franklin', 'SINGLE', 'Soul',          'Atlantic',      '1967-04-14', 10.99, 'Very Good', 6),
  ('Smells Like Teen Spirit',  'Nirvana',        'SINGLE', 'Grunge',        'DGC Records',   '1991-09-10',  7.99, 'Near Mint', 3),
  ('Like a Rolling Stone',    'Bob Dylan',       'SINGLE', 'Folk Rock',     'Columbia',      '1965-07-20', 11.50, 'Good',      4);

-- ── Audit log seed ────────────────────────────────────────────────
INSERT INTO mo_audit_log (actor, actor_role, action, entity_type, entity_id, detail, ip_address, occurred_at) VALUES
  ('admin@musiconline.com',  'ADMIN',    'REGISTER', 'User', '1', 'New account: ADMIN',    '127.0.0.1', NOW()),
  ('admin2@musiconline.com', 'ADMIN',    'REGISTER', 'User', '2', 'New account: ADMIN',    '127.0.0.1', NOW()),
  ('alice@example.com',      'USER',     'REGISTER', 'User', '3', 'New account: USER',     '127.0.0.1', NOW()),
  ('bob@example.com',        'RETAILER', 'REGISTER', 'User', '4', 'New account: RETAILER', '127.0.0.1', NOW()),
  ('carol@example.com',      'USER',     'REGISTER', 'User', '5', 'New account: USER',     '127.0.0.1', NOW()),
  ('dave@example.com',       'RETAILER', 'REGISTER', 'User', '6', 'New account: RETAILER', '127.0.0.1', NOW());

-- ── Verification ──────────────────────────────────────────────────
SELECT CONCAT('✓ Admins:  ', COUNT(*)) AS result FROM mo_admins;
SELECT CONCAT('✓ Users:   ', COUNT(*)) AS result FROM mo_users;
SELECT CONCAT('✓ Vinyls:  ', COUNT(*)) AS result FROM mo_vinyls;
SELECT CONCAT('✓ Audit:   ', COUNT(*)) AS result FROM mo_audit_log;
