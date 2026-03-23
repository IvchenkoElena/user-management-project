# Ручная проверка Docker

В проекте есть полный Docker-набор для всей микросервисной системы.
Используйте шаги ниже, чтобы проверить систему вручную.

## 1) Запуск всех сервисов

Запустите из корня проекта:

```powershell
docker compose up --build -d
```

Проверьте контейнеры:

```powershell
docker compose ps
```

Все сервисы должны быть в статусе `Up (healthy)`.

## 2) Проверка Eureka (Service Discovery)

Откройте в браузере:

```
http://localhost:8761
```

Должны отображаться:
- `USER-SERVICE`
- `NOTIFICATION-SERVICE`
- `API-GATEWAY`

## 3) Проверка Gateway -> User Service

Запросите список пользователей:

```powershell
Invoke-RestMethod -Method Get -Uri http://localhost:8085/api/users
```

Ожидаемый ответ: `[]` или список пользователей.

## 4) Создание пользователя через Gateway

```powershell
$body = @{ name = 'manual-test'; email = 'manual-test@example.com'; age = 25 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8085/api/users -ContentType 'application/json' -Body $body
```

Ожидаемый ответ: JSON пользователя с `id`.

## 5) Проверка уведомления (Mailhog)

Откройте Mailhog:

```
http://localhost:8025
```

Должно появиться письмо на `manual-test@example.com`.

## 6) Остановка всех сервисов (опционально)

```powershell
docker compose down
```

## Swagger (OpenAPI)

Если сервис поднят, Swagger UI доступен по адресам:

- User Service: `http://localhost:8080/swagger-ui.html`
- Notification Service: `http://localhost:8082/swagger-ui.html`

Если используется Gateway, Swagger также доступен через него:

- User Service: `http://localhost:8085/swagger/user/swagger-ui.html`
- Notification Service: `http://localhost:8085/swagger/notification/swagger-ui.html`

## Полезные команды

Показать логи сервиса:

```powershell
docker compose logs --tail=200 user-service
```

Перезапустить один сервис (с пересборкой образа):

```powershell
docker compose up --build -d user-service
```

Очистить данные Kafka, если она не стартует (несовпадение cluster id):

```powershell
docker compose stop kafka
docker rm kafka
docker volume rm user-management-project_kafka_data
docker compose up -d kafka
```
