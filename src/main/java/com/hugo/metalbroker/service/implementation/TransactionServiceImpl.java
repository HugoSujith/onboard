package com.hugo.metalbroker.service.implementation;

import java.sql.Date;
import java.time.LocalDate;
import java.util.logging.Logger;

import com.hugo.metalbroker.exceptions.InsufficientAssets;
import com.hugo.metalbroker.exceptions.InsufficientBalance;
import com.hugo.metalbroker.model.transactions.TradeAssets;
import com.hugo.metalbroker.model.transactions.Transactions;
import com.hugo.metalbroker.model.user.AssetIdDTO;
import com.hugo.metalbroker.model.user.BalanceDTO;
import com.hugo.metalbroker.model.user.UpdateAssetDTO;
import com.hugo.metalbroker.repository.AssetRepo;
import com.hugo.metalbroker.repository.TransactionRepo;
import com.hugo.metalbroker.repository.UserRepo;
import com.hugo.metalbroker.service.TransactionService;
import com.hugo.metalbroker.utils.AssetUtils;
import com.hugo.metalbroker.utils.JWTUtils;
import com.hugo.metalbroker.utils.ProtoUtils;
import com.hugo.metalbroker.utils.UIDGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final UserRepo userRepo;
    private final JWTUtils jwtUtils;
    private final AssetUtils assetUtils;
    private final AssetRepo assetRepo;
    private final UIDGenerator uidGenerator;
    private final ProtoUtils protoUtils;
    private final TransactionRepo transRepo;
    private final UserServiceImpl userService;

    public TransactionServiceImpl(UserRepo userRepo, JWTUtils jwtUtils, AssetUtils assetUtils, AssetRepo assetRepo,
                                  UIDGenerator uidGenerator, ProtoUtils protoUtils, TransactionRepo transRepo,
                                  UserServiceImpl userService) {
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
        this.assetUtils = assetUtils;
        this.assetRepo = assetRepo;
        this.uidGenerator = uidGenerator;
        this.protoUtils = protoUtils;
        this.transRepo = transRepo;
        this.userService = userService;
    }

    @Override
    public Transactions buyAsset(HttpServletRequest request, TradeAssets buyRequest) {
        BalanceDTO fetchBalance = userService.getBalance(request);
        String username = jwtUtils.getUsername(request.getCookies());
        String walletID = jwtUtils.getWalletID(request.getCookies());
        String userCurrencyCode = userRepo.findUserCurrencyCode(walletID);
        double assetValue = assetUtils.getAssetValue(buyRequest, userCurrencyCode, true);

        if (assetValue <= fetchBalance.getBalance()) {
            AssetIdDTO fetchAssetDTO = AssetIdDTO.newBuilder()
                    .setMetal(buyRequest.getMetal())
                    .setWalletId(walletID)
                    .build();
            double currentAssetQuantity = assetRepo.getAssetQuantity(fetchAssetDTO);

            UpdateAssetDTO updateAssetDTO = UpdateAssetDTO.newBuilder()
                    .setWalletId(walletID)
                    .setGrams(currentAssetQuantity + buyRequest.getGrams())
                    .setMetal(buyRequest.getMetal())
                    .build();
            boolean updateQuantity = assetRepo.updateQuantity(updateAssetDTO);

            BalanceDTO updatedBalance = BalanceDTO.newBuilder()
                    .setUsername(username)
                    .setBalance(fetchBalance.getBalance() - assetValue)
                    .build();
            boolean success = userRepo.updateBalance(updatedBalance);

            LocalDate today = LocalDate.now();
            Date todayDate = Date.valueOf(today);

            Transactions transactions = Transactions.newBuilder()
                    .setId(uidGenerator.generateUID(24))
                    .setDatePurchased(protoUtils.sqlDateToGoogleTimestamp(todayDate))
                    .setGrams(buyRequest.getGrams())
                    .setPrice(assetValue)
                    .setStatus("BOUGHT")
                    .setMetal(buyRequest.getMetal())
                    .setUsername(username)
                    .build();
            boolean transactionResult = transRepo.createTransaction(transactions);

            if (updateQuantity && success && transactionResult) {
                return transactions;
            }
            return null;
        }
        throw new InsufficientBalance(Double.toString(fetchBalance.getBalance()));
    }

    @Override
    public Transactions sellAssets(HttpServletRequest request, TradeAssets sellRequest) {
        String walletID = jwtUtils.getWalletID(request.getCookies());
        AssetIdDTO fetchAssetDTO = AssetIdDTO.newBuilder()
                .setMetal(sellRequest.getMetal())
                .setWalletId(walletID)
                .build();
        double assetQuantity = assetRepo.getAssetQuantity(fetchAssetDTO);

        if (assetQuantity >= sellRequest.getGrams()) {
            String username = jwtUtils.getUsername(request.getCookies());
            String userCurrencyCode = userRepo.findUserCurrencyCode(walletID);

            double assetValue = assetUtils.getAssetValue(sellRequest, userCurrencyCode, false);
            BalanceDTO fetchBalance = userService.getBalance(request);

            UpdateAssetDTO assetDTO = UpdateAssetDTO.newBuilder()
                    .setWalletId(walletID)
                    .setGrams(assetQuantity - sellRequest.getGrams())
                    .setMetal(sellRequest.getMetal())
                    .build();
            boolean quantityUpdate = assetRepo.updateQuantity(assetDTO);

            BalanceDTO updatedBalance = BalanceDTO.newBuilder()
                    .setUsername(username)
                    .setBalance(assetValue + fetchBalance.getBalance())
                    .build();
            boolean balanceUpdate = userRepo.updateBalance(updatedBalance);

            Transactions transactions = Transactions.newBuilder()
                    .setId(uidGenerator.generateUID(24))
                    .setDatePurchased(protoUtils.sqlDateToGoogleTimestamp(Date.valueOf(LocalDate.now())))
                    .setGrams(sellRequest.getGrams())
                    .setPrice(assetValue)
                    .setStatus("SOLD")
                    .setMetal(sellRequest.getMetal())
                    .setUsername(username)
                    .build();
            boolean transactionResult = transRepo.createTransaction(transactions);

            if (quantityUpdate && balanceUpdate && transactionResult) {
                return transactions;
            }
            return null;
        } else {
            throw new InsufficientAssets(Double.toString(assetQuantity));
        }
    }
}
