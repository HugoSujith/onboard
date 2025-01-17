CREATE TABLE historic_items (
    date DATE PRIMARY KEY,
    weight_unit VARCHAR(50) NOT NULL,
    open DOUBLE NOT NULL,
    close DOUBLE NOT NULL,
    high DOUBLE NOT NULL,
    low DOUBLE NOT NULL,
    ma50 DOUBLE NULL,
    ma200 DOUBLE NULL
);

CREATE TABLE historic_performance (
      date DATE PRIMARY KEY,
      fived DOUBLE NOT NULL,
      fivey DOUBLE NOT NULL,
      max DOUBLE NOT NULL,
      onem DOUBLE NOT NULL,
      oney DOUBLE NOT NULL,
      teny DOUBLE NOT NULL,
      ytd DOUBLE NOT NULL
);

CREATE TABLE spot_items (
    date DATETIME PRIMARY KEY,
    weight_unit VARCHAR(50) NOT NULL,
    ask DOUBLE NOT NULL,
    mid DOUBLE NOT NULL,
    bid DOUBLE NOT NULL,
    value DOUBLE NOT NULL,
    performance DOUBLE NOT NULL
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date_purchased DATETIME NOT NULL,
    grams DOUBLE NOT NULL,
    price DOUBLE NOT NULL,
    status VARCHAR(256) NOT NULL,
    username VARCHAR(256) NOT NULL
);

CREATE TABLE user (
    username varchar(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    firstname VARCHAR(256) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    balance int NOT NULL
);
