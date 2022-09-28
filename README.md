# api-comparator
```
┌─[fred@MacBook-Pro] - [~] - [2022-09-28 09:36:00]
└─[5] <> xh localhost:8080/good
HTTP/1.1 200 OK
Connection: keep-alive
Content-Length: 25
Content-Length: 25
Content-Type: application/json
Content-Type: application/json; charset=UTF-8
Date: Wed, 28 Sep 2022 19:42:49 GMT
Server: uvicorn

{
    "message": "Hello World"
}


┌─[fred@MacBook-Pro] - [~] - [2022-09-28 09:42:50]
└─[0] <> xh localhost:8080/requests
HTTP/1.1 200 OK
Connection: keep-alive
Content-Length: 140
Content-Type: application/json

[
    {
        "requestId": "8831b3c6-dd82-4b51-89d8-9747cc510223",
        "requestTimestamp": 1664394170206,
        "referenceResponseCode": 200,
        "sampleResponseCode": 200
    }
]


┌─[fred@MacBook-Pro] - [~] - [2022-09-28 09:42:55]
└─[0] <> xh localhost:8080/request/8831b3c6-dd82-4b51-89d8-9747cc510223
HTTP/1.1 200 OK
Connection: keep-alive
Content-Length: 933
Content-Type: application/json

{
    "requestId": "8831b3c6-dd82-4b51-89d8-9747cc510223",
    "requestInfo": {
        "requestId": "8831b3c6-dd82-4b51-89d8-9747cc510223",
        "requestTime": 1664394170206,
        "method": "GET",
        "body": "",
        "url": "/good",
        "headers": {
            "Accept-Encoding": [
                "gzip, deflate, br"
            ],
            "User-Agent": [
                "xh/0.16.1"
            ],
            "Connection": [
                "keep-alive"
            ],
            "Accept": [
                "*/*"
            ],
            "Host": [
                "localhost:8080"
            ]
        }
    },
    "referenceInfo": {
        "responseTime": 1664394170679,
        "requestId": "8831b3c6-dd82-4b51-89d8-9747cc510223",
        "status": 200,
        "body": "{\"message\":\"Hello World\"}",
        "headers": {
            "date": [
                "Wed, 28 Sep 2022 19:42:49 GMT"
            ],
            "server": [
                "uvicorn"
            ],
            "content-length": [
                "25"
            ],
            "content-type": [
                "application/json"
            ]
        }
    },
    "sampleResponseInfo": {
        "responseTime": 1664394170679,
        "requestId": "8831b3c6-dd82-4b51-89d8-9747cc510223",
        "status": 200,
        "body": "{\"message\":\"Hello World\"}",
        "headers": {
            "date": [
                "Wed, 28 Sep 2022 19:42:49 GMT"
            ],
            "server": [
                "uvicorn"
            ],
            "content-length": [
                "25"
            ],
            "content-type": [
                "application/json"
            ]
        }
    },
    "responsesTest": "Equal"
}


┌─[fred@MacBook-Pro] - [~] - [2022-09-28 09:43:20]
└─[0] <>
```
