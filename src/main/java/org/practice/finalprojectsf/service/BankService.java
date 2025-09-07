package org.practice.finalprojectsf.service;

import org.practice.finalprojectsf.entity.Operation;
import org.practice.finalprojectsf.entity.User;
import org.practice.finalprojectsf.repository.OperationRepository;
import org.practice.finalprojectsf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BankService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OperationRepository operationRepository;
    

    public BankOperationResult getBalance(String userId) {
        try {
            Optional<User> userOpt = userRepository.findByUserId(userId);
            if (userOpt.isEmpty()) {
                return new BankOperationResult(-1, "Пользователь не найден");
            }
            
            User user = userOpt.get();
            return new BankOperationResult(1, "Успех", user.getBalance());
            
        } catch (Exception e) {
            return new BankOperationResult(-1, "Ошибка при выполнении операции: " + e.getMessage());
        }
    }

    public BankOperationResult putMoney(String userId, Double amount) {
        try {
            if (amount <= 0) {
                return new BankOperationResult(0, "Сумма должна быть положительной");
            }
            
            Optional<User> userOpt = userRepository.findByUserId(userId);
            User user;
            
            if (userOpt.isEmpty()) {
                // Создаем нового пользователя
                user = new User(userId, amount);
            } else {
                // Пополняем существующего пользователя
                user = userOpt.get();
                user.setBalance(user.getBalance() + amount);
            }
            
            userRepository.save(user);
            Operation op = new Operation();
            op.setUser(user);
            op.setOperationType(1);
            op.setAmount(amount);
            op.setCreatedAt(LocalDateTime.now());
            operationRepository.save(op);
            return new BankOperationResult(1, "Успех");
            
        } catch (Exception e) {
            return new BankOperationResult(0, "Ошибка при выполнении операции: " + e.getMessage());
        }
    }
    

    public BankOperationResult takeMoney(String userId, Double amount) {
        try {
            if (amount <= 0) {
                return new BankOperationResult(0, "Сумма должна быть положительной");
            }
            
            Optional<User> userOpt = userRepository.findByUserId(userId);
            if (userOpt.isEmpty()) {
                return new BankOperationResult(0, "Пользователь не найден");
            }
            
            User user = userOpt.get();
            if (user.getBalance() < amount) {
                return new BankOperationResult(0, "Недостаточно средств");
            }
            
            user.setBalance(user.getBalance() - amount);
            userRepository.save(user);
            Operation op = new Operation();
            op.setUser(user);
            op.setOperationType(2);
            op.setAmount(amount);
            op.setCreatedAt(LocalDateTime.now());
            operationRepository.save(op);
            return new BankOperationResult(1, "Успех");
            
        } catch (Exception e) {
            return new BankOperationResult(0, "Ошибка при выполнении операции: " + e.getMessage());
        }
    }
    
    public List<Operation> getOperationList(String userId, LocalDateTime from, LocalDateTime to) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        User user = userOpt.get();
        if (from == null && to == null) {
            return operationRepository.findByUserOrderByCreatedAtDesc(user);
        }
        return operationRepository.findByUserAndCreatedAtBetweenOptional(user, from, to);
    }


    public static class BankOperationResult {
        private int status;
        private String message;
        private Double balance;
        
        public BankOperationResult(int status, String message) {
            this.status = status;
            this.message = message;
        }
        
        public BankOperationResult(int status, String message, Double balance) {
            this.status = status;
            this.message = message;
            this.balance = balance;
        }
        
        public int getStatus() { return status; }
        public String getMessage() { return message; }
        public Double getBalance() { return balance; }
        
        public void setStatus(int status) { this.status = status; }
        public void setMessage(String message) { this.message = message; }
        public void setBalance(Double balance) { this.balance = balance; }
    }
}
