# ShareIt
[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-green)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Maven-success)](https://maven.apache.org/)
[![Tests](https://img.shields.io/badge/JUnit5-brightgreen)](https://junit.org/junit5/)

ShareIt — сервис для шеринга вещей. Пользователи могут публиковать предметы, бронировать их на время, оставлять 
комментарии и создавать запросы на недостающие вещи.
Проект демонстрирует многомодульную архитектуру (Gateway → Server → DB), валидацию входящих данных и 
использование Spring Boot с JPA.

## Возможности
- Управление пользователями: регистрация, обновление, получение по ID.
- Вещи: добавление, редактирование, поиск по названию и описанию, фильтрация по доступности.
- Бронирование: создание заявок, подтверждение владельцем, получение бронирований по пользователю/владельцу.
- Комментарии: возможность оставить отзыв о вещи после завершённой аренды.
- Запросы на вещи: публикация запросов на недостающие предметы и отклик на них.
- Валидация: проверка входных данных на уровне Gateway.
- Тестирование: юнит- и интеграционные тесты.

## Используемые технологии
- Java 21
- Spring Boot: Web, Data JPA, Validation
- PostgreSQL / H2 (тесты)
- Maven
- Lombok
- JUnit 5, MockMvc
- Docker Compose (подъём сервиса с БД)

## Архитектура
- Gateway — принимает внешние запросы, валидирует их и проксирует в Server.
- Server — основная бизнес-логика (пользователи, вещи, бронирования, комментарии, запросы).
- Common — общие DTO и утилиты.

## Запуск
1.	Склонировать репозиторий:
```
git clone https://github.com/russuAV/java-shareit.git
cd java-shareit
```
2. Собрать:

```mvn clean install```

3. Запустить через Maven:
```
mvn --projects server spring-boot:run
```
   или с Docker:
   ```
   docker-compose up
   ```
4. Приложение будет доступно по адресу: http://localhost:8080

## API (основные эндпоинты)

<details>
  <summary><b>Пользователи</b></summary>

- POST /users — создать
- GET /users/{id} — получить
- PATCH /users/{id} — обновить
- DELETE /users/{id} — удалить
</details>

<details>
  <summary><b>Вещи</b></summary>

-	POST /items — добавить
-	PATCH /items/{id} — обновить
-	GET /items/{id} — получить по ID
-	GET /items/search?text={query} — поиск по названию/описанию

</details>

<details>
  <summary><b>Бронирования</b></summary>

-	POST /bookings — создать
-	PATCH /bookings/{bookingId}?approved={true|false} — подтверждение владельцем
-	GET /bookings/{id} — получить бронирование
-	GET /bookings?state={ALL|CURRENT|PAST|FUTURE|WAITING|REJECTED} — список по пользователю
-	GET /bookings/owner?state=... — список по владельцу вещей

</details>

<details>
  <summary><b>Комментарии</b></summary>

-	POST /items/{itemId}/comment — добавить отзыв (после аренды)

</details>

<details>
  <summary><b>Запросы</b></summary>

-	POST /requests — создать запрос на вещь
-	GET /requests — получить свои запросы
-	GET /requests/all — получить все запросы
-	GET /requests/{id} — получить конкретный запрос

</details>

## Тестирование
```mvn test```