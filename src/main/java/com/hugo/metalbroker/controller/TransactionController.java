package com.hugo.metalbroker.controller;

import com.hugo.metalbroker.model.transactions.TradeAssets;
import com.hugo.metalbroker.model.transactions.Transactions;
import com.hugo.metalbroker.service.implementation.TransactionServiceImpl;
import com.hugo.metalbroker.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class TransactionController {
    private final TransactionServiceImpl transactionService;
    private final JWTUtils jwtUtils;

    public TransactionController(TransactionServiceImpl transactionService, JWTUtils jwtUtils) {
        this.transactionService = transactionService;
        this.jwtUtils = jwtUtils;
    }

    @PutMapping(value = "/buyAsset", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transactions> buyAsset(HttpServletRequest request, @RequestBody TradeAssets asset, HttpServletResponse response) {
        Transactions transactionResponse = transactionService.buyAsset(request, asset);
        jwtUtils.generateRefreshToken(request, response);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/sellAsset", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transactions> sellAsset(HttpServletRequest request, @RequestBody TradeAssets asset, HttpServletResponse response) {
        Transactions transactionResponse = transactionService.sellAssets(request, asset);
        jwtUtils.generateRefreshToken(request, response);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
    }
}
