package com.hugo.metalbroker.repository;

public class SQLQueryConstants {

    // HISTORIC ITEMS
    public static final String INSERT_INTO_HISTORIC_ITEMS
            = "INSERT INTO historic_items (metal, date, open, close, high, low, ma50, ma200, weight_unit) VALUES (:metal, :date, :open, :close, :high, :low, :ma50, :ma200, :weightUnit)";

    public static final String FIND_COUNT_OF_HISTORIC_ITEMS_BY_PK
            = "SELECT COUNT(*) FROM historic_items WHERE date = :date AND metal = :metal";

    public static final String GET_ALL_FROM_HISTORIC_DATA_BY_METAL
            = "SELECT * FROM HISTORIC_ITEMS WHERE metal=:metal ORDER BY date DESC";

    // HISTORIC PERFORMANCE

    public static final String INSERT_INTO_HISTORIC_PERFORMANCE
            = "INSERT INTO historic_performance (date, fived, fivey, max, onem, oney, teny, ytd, metal) VALUES (:date, :fived, :fivey, :max, :onem, :oney, :teny, :ytd, :metal)";

    public static final String GET_ALL_FROM_HISTORIC_PERFORMANCE
            = "SELECT * FROM HISTORIC_PERFORMANCE";

    // SPOT DATA

    public static final String INSERT_INTO_SPOT_ITEMS
            = "INSERT INTO spot_items (metal, date, ask, mid, bid, value, performance, weight_unit) VALUES (:metal, :date, :ask, :mid, :bid, :value, :performance, :weightUnit)";

    public static final String FIND_COUNT_OF_SPOT_ITEMS_BY_PK
            = "SELECT COUNT(*) FROM spot_items WHERE date = :date AND metal = :metal";

    public static final String GET_ALL_FROM_SPOT_ITEMS_BY_METAL
            = "SELECT * FROM SPOT_ITEMS WHERE metal=:metal ORDER BY date DESC";

    public static final String GET_CURRENT_SPOT_PRICES
            = "SELECT * FROM spot_items ORDER BY date DESC LIMIT 1";

    // USER

    public static final String INSERT_INTO_USER =
            "INSERT INTO USER (username, password, firstname, lastname, balance) VALUES (:username, :password, :first_name, :last_name, :balance)";

    public static final String FIND_COUNT_OF_USERS_BY_USERNAME
            = "SELECT COUNT(*) FROM USER WHERE USERNAME = :username";

    public static final String GET_ALL_USERS_BY_USERNAME
            = "SELECT * FROM USER WHERE USERNAME = :username";

    public static final String GET_USER_BALANCE_BY_USERNAME
            = "SELECT balance FROM USER WHERE USERNAME = :username";

    public static final String UPDATE_USER_BALANCE_BY_USERNAME
            = "UPDATE USER SET balance = :balance WHERE username = :username";

    // WALLET

    public static final String INSERT_INTO_WALLET
            = "INSERT INTO wallet (wallet_id, user_id, status, currency_code) VALUES (:wallet_id, :user_id, :status, :currency_code)";

    public static final String FIND_COUNT_OF_WALLETS_BY_USERNAME
            = "SELECT COUNT(*) FROM WALLET WHERE user_id=:username";

    public static final String GET_ALL_WALLETS_FROM_USERNAME
            = "SELECT * FROM WALLET WHERE user_id = :user_id";

    public static final String GET_WALLET_STATUS
            = "SELECT STATUS FROM WALLET WHERE wallet_id = :wallet_id";

    public static final String GET_USER_CURRENCY_CODE_BY_USERNAME
            = "SELECT currency_code FROM wallet WHERE wallet_id = :walletId";

    // USER_WALLET_INFO

    public static final String CREATE_WALLET_INFO_OF_USER
            = "INSERT INTO user_wallet_info(wallet_id, metal, grams) VALUES (:wallet_id, :metal, :grams)";

    public static final String UPDATE_WALLET_DETAILS_BY_WALLET_ID
            = "UPDATE user_wallet_info SET grams = :grams WHERE wallet_id = :wallet_id and metal = :metal";

    public static final String GET_ASSET_QUANTITY_BY_WALLET_ID
            = "SELECT grams FROM user_wallet_info WHERE wallet_id = :wallet_id and metal = :metal";

    public static final String FIND_COUNT_OF_USER_WALLET_INFO_BY_WALLET_ID_AND_METAL
            = "SELECT COUNT(grams) FROM user_wallet_info WHERE wallet_id = :wallet_id and metal = :metal";

    // TRANSACTIONS

    public static final String INSERT_INTO_TRANSACTION
            = "INSERT INTO transactions  (id, date_purchased, grams, price, status, metal, username) "
            + "VALUES (:transaction_id, :date, :grams, :price, :status, :metal, :username)";
}
