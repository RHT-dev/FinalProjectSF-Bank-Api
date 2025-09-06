package org.practice.finalprojectsf;

import org.practice.finalprojectsf.service.BankService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.practice.finalprojectsf.repository")
@EntityScan(basePackages = "org.practice.finalprojectsf.entity")
public class FinalProjectSfApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FinalProjectSfApplication.class, args);
        
        // Получаем сервис из контекста Spring
        BankService bankService = context.getBean(BankService.class);
        
        // Демонстрация работы всех функций
        System.out.println("=== Демонстрация банковских операций ===");
        
        String testUserId = "demo_user_123";
        
        // 1. Получение баланса (пользователь не существует)
        System.out.println("\n1. Получение баланса несуществующего пользователя:");
        BankService.BankOperationResult result1 = bankService.getBalance(testUserId);
        System.out.println("Статус: " + result1.getStatus() + ", Сообщение: " + result1.getMessage());
        
        // 2. Пополнение счета
        System.out.println("\n2. Пополнение счета на 1000 рублей:");
        BankService.BankOperationResult result2 = bankService.putMoney(testUserId, 1000.0);
        System.out.println("Статус: " + result2.getStatus() + ", Сообщение: " + result2.getMessage());
        
        // 3. Получение баланса после пополнения
        System.out.println("\n3. Получение баланса после пополнения:");
        BankService.BankOperationResult result3 = bankService.getBalance(testUserId);
        System.out.println("Статус: " + result3.getStatus() + ", Сообщение: " + result3.getMessage());
        if (result3.getBalance() != null) {
            System.out.println("Баланс: " + result3.getBalance() + " рублей");
        }
        
        // 4. Снятие денег
        System.out.println("\n4. Снятие 300 рублей:");
        BankService.BankOperationResult result4 = bankService.takeMoney(testUserId, 300.0);
        System.out.println("Статус: " + result4.getStatus() + ", Сообщение: " + result4.getMessage());
        
        // 5. Получение финального баланса
        System.out.println("\n5. Финальный баланс:");
        BankService.BankOperationResult result5 = bankService.getBalance(testUserId);
        System.out.println("Статус: " + result5.getStatus() + ", Сообщение: " + result5.getMessage());
        if (result5.getBalance() != null) {
            System.out.println("Баланс: " + result5.getBalance() + " рублей");
        }
        
        // 6. Попытка снять больше, чем есть на счете
        System.out.println("\n6. Попытка снять 1000 рублей (больше баланса):");
        BankService.BankOperationResult result6 = bankService.takeMoney(testUserId, 1000.0);
        System.out.println("Статус: " + result6.getStatus() + ", Сообщение: " + result6.getMessage());
        
        System.out.println("\n=== Демонстрация завершена ===");
        System.out.println("Приложение запущено и готово к работе!");
        System.out.println("REST API доступен по адресу: http://localhost:8080");
    }

}
