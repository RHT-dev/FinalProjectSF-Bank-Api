package org.practice.finalprojectsf.controller;

import org.practice.finalprojectsf.entity.Operation;
import org.practice.finalprojectsf.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bank")
@CrossOrigin(origins = "*")
public class BankController {
    
    @Autowired
    private BankService bankService;
    
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@RequestParam String userId) {
        BankService.BankOperationResult result = bankService.getBalance(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", result.getStatus());
        response.put("message", result.getMessage());
        
        if (result.getBalance() != null) {
            response.put("balance", result.getBalance());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/put-money")
    public ResponseEntity<Map<String, Object>> putMoney(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        Double amount = null;
        
        Object amountObj = request.get("amount");
        if (amountObj instanceof Number) {
            amount = ((Number) amountObj).doubleValue();
        } else if (amountObj instanceof String) {
            try {
                amount = Double.parseDouble((String) amountObj);
            } catch (NumberFormatException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 0);
                errorResponse.put("message", "Неверный формат суммы");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }
        
        if (userId == null || amount == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 0);
            errorResponse.put("message", "Отсутствуют обязательные параметры: userId и amount");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        BankService.BankOperationResult result = bankService.putMoney(userId, amount);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", result.getStatus());
        response.put("message", result.getMessage());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/take-money")
    public ResponseEntity<Map<String, Object>> takeMoney(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        Double amount = null;
        

        Object amountObj = request.get("amount");
        if (amountObj instanceof Number) {
            amount = ((Number) amountObj).doubleValue();
        } else if (amountObj instanceof String) {
            try {
                amount = Double.parseDouble((String) amountObj);
            } catch (NumberFormatException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 0);
                errorResponse.put("message", "Неверный формат суммы");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }
        
        if (userId == null || amount == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 0);
            errorResponse.put("message", "Отсутствуют обязательные параметры: userId и amount");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        BankService.BankOperationResult result = bankService.takeMoney(userId, amount);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", result.getStatus());
        response.put("message", result.getMessage());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-operations")
    public ResponseEntity<?> getOperations(
            @RequestParam String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        try {
            List<Operation> operations = bankService.getOperationList(userId, from, to);
            return ResponseEntity.ok(operations);
        } catch (IllegalArgumentException ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 0);
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/transfer-money")
    public ResponseEntity<Map<String, Object>> transferMoney(@RequestBody Map<String, Object> request) {
        String fromUserId = (String) request.get("fromUserId");
        String toUserId = (String) request.get("toUserId");
        Double amount = null;

        Object amountObj = request.get("amount");
        if (amountObj instanceof Number) {
            amount = ((Number) amountObj).doubleValue();
        } else if (amountObj instanceof String) {
            try {
                amount = Double.parseDouble((String) amountObj);
            } catch (NumberFormatException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 0);
                errorResponse.put("message", "Неверный формат суммы");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }

        if (fromUserId == null || toUserId == null || amount == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 0);
            errorResponse.put("message", "Отсутствуют обязательные параметры: fromUserId, toUserId и amount");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        BankService.BankOperationResult result = bankService.transferMoney(fromUserId, toUserId, amount);

        Map<String, Object> response = new HashMap<>();
        response.put("status", result.getStatus());
        response.put("message", result.getMessage());
        return ResponseEntity.ok(response);
    }
}
