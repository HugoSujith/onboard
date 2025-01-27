CREATE DATABASE IF NOT EXISTS onboard;

use onboard;

CREATE TABLE IF NOT EXISTS historic_items (
    date DATE NOT NULL,
    metal VARCHAR(10) NOT NULL,
    weight_unit VARCHAR(50) NOT NULL,
    open DOUBLE NOT NULL,
    close DOUBLE NOT NULL,
    high DOUBLE NOT NULL,
    low DOUBLE NOT NULL,
    ma50 DOUBLE NULL,
    ma200 DOUBLE NULL,
    PRIMARY KEY (date, metal)
);

CREATE TABLE IF NOT EXISTS spot_items (
    date DATETIME NOT NULL,
    metal VARCHAR(64) NOT NULL,
    weight_unit VARCHAR(50) NOT NULL,
    ask DOUBLE NOT NULL,
    mid DOUBLE NOT NULL,
    bid DOUBLE NOT NULL,
    value DOUBLE NOT NULL,
    performance DOUBLE NOT NULL,
    PRIMARY KEY (date, metal)
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
    metal VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date_purchased DATETIME NOT NULL,
    grams DOUBLE NOT NULL,
    price DOUBLE NOT NULL,
    status VARCHAR(256) NOT NULL,
    metal VARCHAR(64) NOT NULL,
    username VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    firstname VARCHAR(256) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    balance INT NOT NULL
);
