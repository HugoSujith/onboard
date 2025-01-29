package com.hugo.metalbroker.service;

import java.util.Map;

import com.hugo.metalbroker.model.transactions.TradeAssets;
import com.hugo.metalbroker.model.transactions.Transactions;
import com.hugo.metalbroker.model.user.BalanceDTO;
import com.hugo.metalbroker.model.user.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    public Map.Entry<UserDTO, String> login(UserDTO user, HttpServletResponse response);

    boolean addUsersToDB(UserDTO user);

    BalanceDTO getBalance(HttpServletRequest request);
}
