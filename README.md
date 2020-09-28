# Инструкция по запуску

Для запуска требуется зайти в папку с проектом и выполнить команды:

linux
```bash
./mvnw install
./mvnw spring-boot:run
```

windows
```bash
mvnw.cmd install
mvnw.cmd spring-boot:run
```

# Инструкция по API

POST http://localhost:8080/api/post/longUrl

В теле запроса нужно укзаать "url", пример тела:
```JSON
{
  "url": "https://start.avito.ru/tech"
}
```

В ответ придёт JSON со следующими ключами:

status - статус выполнения

expireAfter - через сколько дней истекает действие ссылки(не используется)

shortUrl - короткий url по которому произойдёт переадресация(для браузера)

apiUrl - url переадресации для api

longUrl - ссылка, которая была получена

В случае, если ссылка не является валидной, возвращает status 400 - bad request

---

POST http://localhost:8080/api/post/customShortPath

В теле запроса нужно укзаать "url" и "shortUserPath", пример тела:
```JSON
{
  "url": "https://github.com/avito-tech/auto-backend-trainee-assignment",
  "shortUserPath": "avito-auto-be"
}
```
Ответ совпадает с предыдущим.

В случае, если ссылка или сокращение не являются валидными, возвращает status 400 - bad request

---

GET http://localhost:8080/api/redirect/{shortPath}

Где {shortPath} - сокращение

Вернёт status выполнения и ссылку, на которую ведёт сокращение

---

GET http://localhost:8080/{shortPath}

Если сокращение существует, то происходит переадресация, иначе 404.