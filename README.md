# Клубная карта

## Описание проекта

**Клубная карта** – сервис для единой аутентификации пользователей и управления их членством в клубах и организациях. 
Система предоставляет механизм регистрации, авторизации, а также управление ролями пользователей и уровнями привилегий. 

## Основные возможности

- **Регистрация и аутентификация**: JWT-токены, многофакторная аутентификация.
- **Ролевая модель**: пользователь, администратор, супер-администратор.
- **Виртуальные карты**: создание, кастомизация и управление виртуальными картами.
- **Привилегии**: уровни доступа (стандарт, повышенный, VIP).
- **QR-коды**: генерация и использование для аутентификации.
- **Интеграция**: передача данных об аутентификации в **TechRadar**.

## Технологический стек

- **Backend**: Java 17, Spring Boot 3
- **База данных**: PostgreSQL
- **Безопасность**: JWT, Spring Security
- **Документация API**: Swagger (Springdoc OpenAPI 2.2.0)
- **Тестирование**: JUnit, Mockito
- **Логирование**: Lombok (Slf4j)
- **Контейнеризация**: Docker
- **Асинхронные процессы**: Kafka

## API Endpoints

| Метод    | URL                             | Описание                                  | Статус      |
| -------- | ------------------------------ | ----------------------------------------- | ----------- |
| `POST`   | `/api/auth/register`           | Регистрация пользователя                 | ✅ Сделано   |
| `POST`   | `/api/auth/login`              | Вход в систему                           | ✅ Сделано   |
| `POST`   | `/api/auth/logout`             | Выход из системы                         | ✅ Сделано   |
| `POST`   | `/api/auth/refresh`            | Получение нового JWT-токена              | ✅ Сделано   |
| `GET`    | `/api/auth/validate`           | Проверка JWT токена                      | ✅ Сделано   |
| `POST`   | `/api/user/role`               | Изменение роли пользователя              | ✅ Сделано  |
| `GET`    | `/api/user/profile`            | Просмотр профиля пользователя            | ✅ Сделано  |
| `PATCH`  | `/api/user/profile`            | Редактирование профиля                   | ✅ Сделано  |
| `POST`   | `/api/card`                    | Создание виртуальной карты               | IN PROGRESS |
| `GET`    | `/api/card/{id}`               | Получение информации о карте             | IN PROGRESS |
| `PATCH`  | `/api/card/{id}`               | Кастомизация виртуальной карты           | IN PROGRESS |
| `POST`   | `/api/card/qrcode`             | Генерация QR-кода для карты              | IN PROGRESS |

## Установка и запуск

### Локальный запуск

1. Склонируйте репозиторий:
   ```sh
   git clone https://github.com/asem25/ClubCard.git
   ```
2. Перейдите в каталог проекта:
   ```sh
   cd ClubCard
   ```
3. Соберите и запустите приложение:
   ```sh
   ./mvnw spring-boot:run
   ```

### Запуск в Docker

1. Соберите Docker-образ:
   ```sh
   docker build -t clubcard .
   ```
2. Запустите контейнер:
   ```sh
   docker run -p 8080:8080 clubcard
   ```

## Разработка и тестирование

### Запуск тестов

```sh
./mvnw test
```

## Документация API

Документация доступна по адресу:

```
http://localhost:8080/swagger-ui.html
        {IN PROGRESS}
```

## Интеграция с TechRadar

- Передача данных об аутентифицированных пользователях.
- **TechRadar** использует эти данные для персонализации контента и настройки уровней доступа.
- Интеграция осуществляется через стандартные API-аутентификации (`/api/auth/*`).

## Контакты

Для вопросов и предложений пишите на [**asemavin250604@gmail.com**](mailto:asemavin250604@gmail.com).
