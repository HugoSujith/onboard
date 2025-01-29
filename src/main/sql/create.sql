DROP DATABASE IF EXISTS onboard;

CREATE DATABASE IF NOT EXISTS onboard;

USE onboard;

CREATE TABLE IF NOT EXISTS historic_items (
    date DATE NOT NULL,
    metal VARCHAR(50) NOT NULL,
    weight_unit VARCHAR(50) NOT NULL,
    open DOUBLE NOT NULL,
    close DOUBLE NOT NULL,
    high DOUBLE NOT NULL,
    low DOUBLE NOT NULL,
    ma50 DOUBLE NULL,
    ma200 DOUBLE NULL,
    PRIMARY KEY (date, metal),
    INDEX (metal)
);

CREATE TABLE IF NOT EXISTS spot_items (
    date TIMESTAMP NOT NULL,
    metal VARCHAR(64) NOT NULL,
    weight_unit VARCHAR(50) NOT NULL,
    ask DOUBLE NOT NULL,
    mid DOUBLE NOT NULL,
    bid DOUBLE NOT NULL,
    value DOUBLE NOT NULL,
    performance DOUBLE NOT NULL,
    PRIMARY KEY (date, metal),
    INDEX (metal)
);

CREATE TABLE IF NOT EXISTS historic_performance (
    date DATE PRIMARY KEY,
    fived DOUBLE NOT NULL,
    fivey DOUBLE NOT NULL,
    max DOUBLE NOT NULL,
    onem DOUBLE NOT NULL,
    oney DOUBLE NOT NULL,
    teny DOUBLE NOT NULL,
    ytd DOUBLE NOT NULL,
    metal VARCHAR(64) NOT NULL,
    INDEX (metal)
);

CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    firstname VARCHAR(256) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    balance DOUBLE NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(24) PRIMARY KEY,
    date_purchased TIMESTAMP NOT NULL,
    grams DOUBLE NOT NULL,
    price DOUBLE NOT NULL,
    status VARCHAR(256) NOT NULL,
    metal VARCHAR(64) NOT NULL,
    username VARCHAR(255) NOT NULL,
    FOREIGN KEY (username) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS currency (
    currency_code CHAR(3),
    country_name VARCHAR(256),
    PRIMARY KEY (currency_code),
    UNIQUE (country_name)
);

CREATE TABLE IF NOT EXISTS wallet (
    wallet_id VARCHAR(30),
    user_id VARCHAR(255),
    currency_code CHAR(3),
    PRIMARY KEY (wallet_id, user_id, currency_code),
    status ENUM('ACTIVE', 'INACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (currency_code) REFERENCES currency(currency_code),
    UNIQUE (user_id),
    UNIQUE (wallet_id),
    UNIQUE (currency_code)
);

CREATE TABLE IF NOT EXISTS user_wallet_info (
    wallet_id VARCHAR(30),
    metal VARCHAR(64),
    grams DOUBLE,
    PRIMARY KEY (wallet_id, metal),
    FOREIGN KEY (wallet_id) REFERENCES wallet(wallet_id) ON DELETE CASCADE ON UPDATE CASCADE
);

insert into currency (currency_code, country_name) VALUES ("INR", "INDIA");

