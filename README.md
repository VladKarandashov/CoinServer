# CoinServer

## Возможные ошибки

```java
    // отбрасывается исключительно в непонятных технических случаях
    INTERNAL_SERVER_ERROR(1000),

    TOKEN_HEADER_NOT_FOUND(1101), // если в запросе нет токена - идём login
    WRONG_TOKEN(1102), // в БД нет user с таким токеном - вы зашли с другого устройства
    TOKEN_EXPIRED_NEED_LOGIN(1103), // токен есть, но старый - идём login
    WRONG_LOGIN_OR_PASSWORD(1104), // нет user с такими логин и пароль
    LOGIN_ALREADY_EXIST(1105),  // если при регистрации обнаружился user с таким же именем

    VALIDATION_ERROR(1200), // общая ошибка валидации
    VALIDATION_ERROR_USD(1201), // нельзя купить акций на неположительное кол-во денег
    VALIDATION_ERROR_ASSETS(1202), // нельзя продать неположительное кол-во акций

    NOT_FOUND(1301), // валюта не обнаружена - операция сейчас недоступна
    LACK_OF_RESOURCES(1302), // пользователь не может продать акций, больше чем у него есть
```

## Открытые api доступные всем желающим без доп условий

### API для получения курсов монет

#### GET /api/v1/coins/prices
Возвращает название каждой монеты и её текущую цену в долларах

Пример ответа:

```http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 06 Nov 2023 11:56:54 GMT
Keep-Alive: timeout=60
Connection: keep-alive

[
  {
    "symbol": "BTC",
    "price": "35224.17000000"
  },
  {
    "symbol": "ETH",
    "price": "1898.08000000"
  },
  {
    "symbol": "BNB",
    "price": "245.20000000"
  }
  ......
]
```

#### GET /api/v1/coins/prices/{symbol}?interval=3m
Возвращает список из цен определённой монеты за определённый срок

Вместо {symbol} необходимо поставить обозначение необходимой монеты. 
Например: GET /api/v1/coins/prices/BTC
С помощью параметра interval можно задать необходимый интервал, для которого нужны цены.

**Если не указывать интервал - вернутся цены за последние три дня**

Поддерживаются интервалы: 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M

| m | минуты |
|---|--------|
| h | часы   |
| d | дни    |
| w | недели |
| M | месяцы |

Если передать неподходящий интервал - будет дефолтный (за три последних дня)

**Уменьшение/увеличение интервала не ведёт к уменьшению/увеличению "точек" в response! 
Изменится только промежуток между каждой точкой!**

Пример ответа:

```http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 06 Nov 2023 12:01:31 GMT
Keep-Alive: timeout=60
Connection: keep-alive

[
  {
    "openTime": 1569801600000,
    "open": "169.26000000",
    "high": "185.53000000",
    "low": "165.01000000",
    "close": "180.24000000",
    "volume": "1538536.04307000",
    "closeTime": 1570060799999,
    "quoteAssetVolume": "271339166.93771000",
    "numberOfTrades": 440543,
    "takerBuyBaseAssetVolume": "778920.06217000",
    "takerBuyQuoteAssetVolume": "137371854.23944880"
  },
  {
    "openTime": 1570060800000,
    "open": "180.24000000",
    "high": "180.72000000",
    "low": "169.55000000",
    "close": "176.25000000",
    "volume": "967143.04125000",
    "closeTime": 1570319999999,
    "quoteAssetVolume": "169118702.06530830",
    "numberOfTrades": 281110,
    "takerBuyBaseAssetVolume": "497871.90534000",
    "takerBuyQuoteAssetVolume": "87073224.91660420"
  },
  .........
]
```

#### GET /api/v1/coins/statistics/{symbol}
Возвращает статистику по конкретной монете (общая информация)

Пример ответа:

```http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 06 Nov 2023 12:15:40 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "priceChange": "6.15000000",
  "priceChangePercent": "0.326",
  "weightedAvgPrice": "1886.17282972",
  "prevClosePrice": "1887.09000000",
  "lastPrice": "1893.24000000",
  "bidPrice": "1893.23000000",
  "askPrice": "1893.24000000",
  "openPrice": "1887.09000000",
  "highPrice": "1912.67000000",
  "lowPrice": "1855.45000000",
  "volume": "343001.29790000",
  "openTime": 1699186540297,
  "closeTime": 1699272940297,
  "firstId": 1223815766,
  "lastId": 1224551659,
  "count": 735894
}
```

### API для получения лидер-борда

#### GET /api/v1/statistics/leaderboard
Возвращает для каждого пользователя: сколько долларов он потратил (spentUsd), сколько стоят в долларах все его активы (costUsd) и процент выигрыша/проигрыша

Пример ответа:

```http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 06 Nov 2023 12:19:08 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "statusCode": 0,
  "message": "SUCCESS",
  "data": {
    "userScoreList": [
      {
        "login": "admin",
        "changeCost": {
          "spentUsd": 0,
          "costUsd": 0,
          "percent": 0
        }
      }
    ]
  }
}
```



## API для входа или регистрации

### Вход или регистрация

#### POST /api/v1/auth/registration
Позволяет зарегистрироваться на платформе.
Принимает желаемый логин и пароль по паттерну: "^[a-zA-Z0-9]{1,32}$"

```http request
content-type: application/json

{
  "login": "testLogin123",
  "password": "qwerty123"
}
```

Возможные ответы:
```http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 06 Nov 2023 12:27:51 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "statusCode": 0,
  "message": "SUCCESS",
  "data": {
    "token": "59b2fe01-96bb-471f-b99c-eb69bd94c5fd"
  }
}
```
```http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 06 Nov 2023 12:29:38 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "statusCode": 1105,
  "message": "LOGIN_ALREADY_EXIST",
  "data": null
}
```

Полное описание status-кодов можно найти в начале документа!
Токен необходимо запомнить!




#### POST /api/v1/auth/login

Абсолютно тоже самое, что и при регистации. Возможные коды ответов см в начале документа.

## API доступны только авторизованным пользователям
**Обязательно необходим хедер token - в который нужно передать запомненный uuid**

### Получение информации о пользователе

#### GET /api/v1/balance
Возвращает в поле data - оставшиеся доллары на счету

```http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 06 Nov 2023 12:38:51 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "statusCode": 0,
  "message": "SUCCESS",
  "data": 10000
}
```

#### GET /api/v1/balance/assets
возвращает баланс по каждой монете, которую купил пользователь

// TODO response (можешь сам дёрнуть и посмотреть)

#### GET /api/v1/balance/assets/{symbol}
то же самое что и предыдущая, но по конкретной монете (чтобы всё не запрашивать)

#### GET /api/v1/balance/info
полная инфа о пользователе = /api/v1/balance + /api/v1/balance/assets

```http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 06 Nov 2023 12:44:03 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "statusCode": 0,
  "message": "SUCCESS",
  "data": {
    "usdMoney": 10000,
    "assets": [],
    "changeCost": {
      "spentUsd": 0,
      "costUsd": 0,
      "percent": 0
    }
  }
}
```


### Покупка/продажа активов

TODO