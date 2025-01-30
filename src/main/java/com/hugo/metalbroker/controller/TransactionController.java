package com.hugo.metalbroker.controller;

import com.hugo.metalbroker.model.transactions.TradeAssets;
import com.hugo.metalbroker.model.transactions.Transactions;
import com.hugo.metalbroker.service.implementation.TransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
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

    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PutMapping(value = "/buyAsset", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transactions> buyAsset(HttpServletRequest request, @RequestBody TradeAssets asset) {
        Transactions response = transactionService.buyAsset(request, asset);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/sellAsset", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sellAsset(HttpServletRequest request, @RequestBody TradeAssets asset) {
        Transactions response = transactionService.sellAssets(request, asset);
        if (response == null) {
            return new ResponseEntity<>("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }
}
