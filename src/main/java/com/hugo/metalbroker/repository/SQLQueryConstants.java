package com.hugo.metalbroker.repository;

public class SQLQueryConstants {
    public static final String FIND_COUNT_OF_HISTORIC_ITEMS_BY_PK
            = "SELECT COUNT(*) FROM historic_items WHERE date = :date AND metal = :metal";

    public static final String GET_ALL_FROM_HISTORIC_DATA_BY_METAL
            = "SELECT * FROM HISTORIC_ITEMS WHERE metal=:metal";

    public static final String INSERT_INTO_HISTORIC_ITEMS
            = "INSERT INTO historic_items (metal, date, open, close, high, low, ma50, ma200, weight_unit) VALUES (:metal, :date, :open, :close, :high, :low, :ma50, :ma200, :weightUnit)";

    public static final String GET_ALL_FROM_HISTORIC_PERFORMANCE
            = "SELECT * FROM HISTORIC_PERFORMANCE";

    public static final String INSERT_INTO_HISTORIC_PERFORMANCE
            = "INSERT INTO historic_performance (date, fived, fivey, max, onem, oney, teny, ytd, metal) VALUES (:date, :fived, :fivey, :max, :onem, :oney, :teny, :ytd, :metal)";

    public static final String FIND_COUNT_OF_SPOT_ITEMS_BY_PK
            = "SELECT COUNT(*) FROM spot_items WHERE date = :date AND metal = :metal";

    public static final String GET_ALL_FROM_SPOT_ITEMS_BY_METAL
            = "SELECT * FROM SPOT_ITEMS WHERE metal=:metal";

    public static final String INSERT_INTO_USER =
            "INSERT INTO USER (username, password, firstname, lastname, balance) VALUES (:username, :password, :first_name, :last_name, :balance)";

    public static final String GET_ALL_USERS_BY_USERNAME
            = "SELECT * FROM USER WHERE USERNAME = :username";

    public static final String FIND_COUNT_OF_USERS_BY_USERNAME
            = "SELECT COUNT(*) FROM USER WHERE USERNAME = :username";

    public static final String INSERT_INTO_WALLET
            = "INSERT INTO wallet (wallet_id, user_id, status) VALUES (:wallet_id, :user_id, :status)";

    public static final String FIND_COUNT_OF_WALLETS_BY_USERNAME
            = "SELECT COUNT(*) FROM WALLET WHERE user_id=:username";

    public static final String GET_ALL_WALLETS_FROM_USERNAME
            = "SELECT * FROM WALLET WHERE user_id = :user_id";
}
