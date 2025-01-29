package com.hugo.metalbroker.service;

import com.hugo.metalbroker.model.transactions.TradeAssets;
import com.hugo.metalbroker.model.transactions.Transactions;
import jakarta.servlet.http.HttpServletRequest;

public interface TransactionService {
    Transactions buyAsset(HttpServletRequest request, TradeAssets buyRequest);

    Transactions sellAssets(HttpServletRequest request, TradeAssets sellRequest);
}
