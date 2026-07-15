# Relationship Manager Backend

微信小程序人脉管理后端，基于 Spring Boot 3、MyBatis、MySQL 和 JWT。

## 已实现功能

- 微信小程序 `code` 登录：服务端调用微信 `jscode2session` 获取 `openid`
- 首次登录自动创建用户，后续登录直接返回已有用户
- JWT 签发与请求过滤，过期或伪造的 token 返回 401
- 联系人新增、分页查询、关键词搜索、详情、修改和删除
- 联系互动记录新增、分页查询、修改和删除，自动展示最近联系时间和方式
- 所有联系人 SQL 均带 `user_id`，不同用户的数据相互隔离
- Jakarta Validation 参数校验和统一异常响应
- 微信 `session_key` 不返回给小程序，登录响应也不暴露 `openid`

## 运行环境

- JDK 17 或更高版本
- Maven 3.9+
- MySQL 8（也兼容大多数 MySQL 5.7 环境）

## 1. 初始化数据库

```bash
mysql -u root -p < src/main/resources/schema.sql
```

## 2. 配置环境变量

可参考 `.env.example`。至少需要配置：

```bash
export DB_USERNAME=root
export DB_PASSWORD='你的数据库密码'
export WECHAT_APP_ID='你的小程序AppID'
export WECHAT_APP_SECRET='你的小程序AppSecret'
export JWT_SECRET='至少32字节的随机密钥，请勿提交到Git'
```

生成 JWT 密钥的示例：

```bash
openssl rand -base64 48
```

## 3. 启动

```bash
mvn spring-boot:run
```

默认地址为 `http://localhost:8080`。

## 登录接口

小程序端先调用：

```javascript
wx.login({
  success(loginResult) {
    wx.request({
      url: 'https://你的域名/api/auth/wechat-login',
      method: 'POST',
      data: { code: loginResult.code },
      success(response) {
        wx.setStorageSync('token', response.data.data.token)
      }
    })
  }
})
```

请求：

```http
POST /api/auth/wechat-login
Content-Type: application/json

{"code":"wx.login返回的临时code"}
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJ...",
    "tokenType": "Bearer",
    "expiresIn": 604800,
    "user": {
      "id": 1,
      "nickname": "微信用户"
    }
  }
}
```

## 联系人接口

除登录外的接口都要携带：

```http
Authorization: Bearer <token>
```

| 方法 | 地址 | 功能 |
|---|---|---|
| `POST` | `/api/contacts` | 新增联系人 |
| `GET` | `/api/contacts?page=1&size=20&keyword=张` | 分页搜索 |
| `GET` | `/api/contacts/{id}` | 查询详情 |
| `PUT` | `/api/contacts/{id}` | 修改联系人 |
| `DELETE` | `/api/contacts/{id}` | 删除联系人 |

新增或修改请求体：

```json
{
  "name": "张三",
  "relationshipType": "朋友",
  "birthday": "2000-01-01"
}
```

联系人列表和详情会根据最新的互动记录返回：

```json
{
  "lastContactMethod": "wechat",
  "lastContactedAt": "2026-07-15T14:00:00"
}
```

没有互动记录时，这两个字段为 `null`。

## 联系互动记录接口

| 方法 | 地址 | 功能 |
|---|---|---|
| `POST` | `/api/contacts/{contactId}/interactions` | 新增联系记录 |
| `GET` | `/api/contacts/{contactId}/interactions?page=1&size=20` | 按联系时间倒序分页查询 |
| `PUT` | `/api/contacts/{contactId}/interactions/{interactionId}` | 修改联系记录 |
| `DELETE` | `/api/contacts/{contactId}/interactions/{interactionId}` | 删除联系记录 |

新增或修改联系记录的请求体：

```json
{
  "contactMethod": "wechat",
  "contactedAt": "2026-07-15T14:00:00",
  "notes": "讨论了下周见面的安排"
}
```

`contactMethod` 支持：

| 值 | 含义 |
|---|---|
| `wechat` | 微信 |
| `phone` | 电话 |
| `sms` | 短信 |
| `in_person` | 见面 |
| `email` | 邮件 |
| `other` | 其他 |

`contactedAt` 是实际联系发生的时间，`createdAt` 是记录写入系统的时间。删除联系人时，其互动记录会由数据库外键自动删除。

## 上线前注意

- 必须使用 HTTPS，并在微信公众平台配置合法 request 域名。
- 必须通过环境变量覆盖默认 `JWT_SECRET`。
- 不要把 `AppSecret` 放在小程序代码、Git 或接口响应中。
- 当前实现是单一长效 JWT；如果以后需要强制下线或多设备管理，可增加 Refresh Token 与 Redis 黑名单。
