package com.hugo.metalbroker.service;

import com.hugo.metalbroker.model.transactions.TradeAssets;
import com.hugo.metalbroker.model.transactions.Transactions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TransactionService {
    Transactions buyAsset(HttpServletRequest request, TradeAssets buyRequest);

    Transactions sellAssets(HttpServletRequest request, TradeAssets sellRequest);
}
