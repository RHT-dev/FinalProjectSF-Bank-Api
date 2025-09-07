# Банковское REST API - Инструкции по использованию

## Первичная настройка
1. Скачайте проект
```bash
git clone https://github.com/RHT-dev/FinalProjectSF-Bank-Api.git
```
2. Установите PostgreSQL.
3. Создайте базу данных:
```sql
CREATE DATABASE bank_db;
```
4. Настройте подключение к БД:
   
**Обновите настройки** в `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bank_db
spring.datasource.username=your_postgres_name
spring.datasource.password=your_postgres_password
```
   


## Запуск приложения

**Способ 1:**
- Запустить проект через терминал с помощью Maven
```
mvn spring-boot:run
```
**Способ 2:**
- Запустить класс ```FinalProjectSfApplication.java``` с помощью IDEA

Приложение будет доступно по адресу: http://localhost:8080

**Примечание:** При запуске приложения в **консоли** будет показана демонстрация всех банковских операций:
- Получение баланса несуществующего пользователя
- Пополнение счета
- Получение баланса после пополнения
- Снятие денег
- Получение финального баланса
- Попытка снять больше, чем есть на счете

## API Endpoints

### 1. Получить баланс пользователя

**GET** `/api/bank/balance`

**Параметры:**
- `userId` (query parameter) - ID пользователя

**Пример запроса:**
```
GET http://localhost:8080/api/bank/balance?userId=demo_user_123
```

**Пример ответа:**
```json
{
  "status": 1,
  "message": "Успех",
  "balance": 1000.0
}
```

**Коды статуса:**
- `1` - Успех
- `-1` - Ошибка (пользователь не найден или ошибка выполнения)

### 2. Пополнить счет

**POST** `/api/bank/put-money`

**Тело запроса:**
```json
{
  "userId": "demo_user_123",
  "amount": 500.0
}
```

**Пример ответа:**
```json
{
  "status": 1,
  "message": "Успех"
}
```

**Коды статуса:**
- `1` - Успех
- `0` - Ошибка (неверная сумма или ошибка выполнения)

### 3. Снять деньги со счета

**POST** `/api/bank/take-money`

**Тело запроса:**
```json
{
  "userId": "demo_user_123",
  "amount": 200.0
}
```

**Пример ответа:**
```json
{
  "status": 1,
  "message": "Успех"
}
```

**Коды статуса:**
- `1` - Успех
- `0` - Ошибка (недостаточно средств, пользователь не найден или ошибка выполнения)

## Структура базы данных
1. **Скриншот структуры БД** находится в корне проекта, файл: `structude_db.png`
2. **Dump-файлы БД** находятся в корне проекта: `bank_db_dump.sql`, `bank_db_dump_stage2.sql`

#### Таблица `users`:
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `user_id` (VARCHAR, UNIQUE, NOT NULL)
- `balance` (DOUBLE, NOT NULL, DEFAULT 0.0)

#### Таблица `operations`:
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `user_fk` (BIGINT, NOT NULL, FK -> `users.id`, ON DELETE CASCADE)
- `operation_type` (INTEGER, NOT NULL) — 1 = пополнение, 2 = списание
- `counterparty_user_fk` (BIGINT, NULL, FK -> `users.id`) — контрагент для переводов
- `amount` (DOUBLE, NOT NULL)
- `created_at` (TIMESTAMP, NOT NULL)

## Особенности реализации

1. **Автоматическое создание пользователей**: При пополнении счета, если пользователь не существует, он будет создан автоматически
2. **Валидация данных**: Проверка положительности сумм и обязательных параметров
3. **Транзакционность**: Все операции выполняются в транзакциях
4. **Обработка ошибок**: Сообщения об ошибках для отладки

### 4. Список операций пользователя

**GET** `/api/bank/get-operations`

Параметры:
- `userId` (query) — обязательный
- `from` (query, ISO 8601, опционально) — напр. `2024-01-01T00:00:00`
- `to` (query, ISO 8601, опционально) — напр. `2024-12-31T23:59:59`

Если даты не переданы — возвращаются все операции пользователя (включая переводы, поле контрагента присутсвует в сущности).

### 5. Перевод денег между пользователями

**POST** `/api/bank/transfer-money`

Тело запроса:
```json
{
  "fromUserId": "alice",
  "toUserId": "bob",
  "amount": 50.0
}
```

Ответ при успехе:
```json
{
  "status": 1,
  "message": "Успех"
}
```

Перевод выполняется атомарно (в одной транзакции): изменяются балансы обоих пользователей и создаются две записи в `operations` (списание у отправителя, зачисление у получателя) с заполненным `counterparty_user_fk`.
