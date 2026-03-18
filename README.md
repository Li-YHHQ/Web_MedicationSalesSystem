# 医疗用品销售系统 (Medication Sales System)

> 基于 Spring Boot 3.4 + MyBatis 的现代化医药电商平台

## 📋 项目简介

这是一个功能完整的**医药电商平台后端系统**，采用前后端分离架构，提供完整的商品管理、购物车、订单处理、用户认证等功能。系统支持普通用户购药和管理员后台管理两种角色，实现了从商品浏览到订单完成的完整业务流程。

### 核心业务流程

```
用户注册/登录 → 浏览药品 → 添加购物车 → 提交订单 → 支付 → 发货 → 收货确认 → 评价
```

## ✨ 主要功能

### 用户端功能
- **用户管理**：注册、登录、个人资料管理
- **商品浏览**：分类浏览、关键词搜索、价格筛选、处方药筛选
- **购物车**：添加商品、修改数量、删除商品、清空购物车
- **订单管理**：下单、支付、取消订单、查看订单详情、确认收货
- **商品评价**：订单完成后评价商品、查看商品评价

### 管理端功能
- **药品管理**：添加、修改、删除、上下架药品
- **订单管理**：查看所有订单、订单发货、订单统计
- **分类管理**：添加、编辑、删除商品分类
- **轮播管理**：首页轮播图管理
- **用户管理**：查看用户信息、用户状态管理

## 🏗️ 技术架构

### 系统架构图

```
┌─────────────────┐      ┌──────────────────────┐      ┌─────────────┐
│   前端应用       │      │   Spring Boot 后端   │      │   MySQL     │
│   (任意前端)     │◄────►│   RESTful API        │◄────►│   数据库    │
│                 │ HTTP │   + JWT认证          │ JDBC │             │
└─────────────────┘      └──────────────────────┘      └─────────────┘
```

### 技术栈

#### 后端框架
- **Spring Boot 3.4.12** - 核心框架
- **MyBatis 3.0.5** - ORM 持久层框架
- **Spring Security Crypto** - 密码加密
- **Spring Validation** - 数据验证

#### 数据库
- **MySQL 8.0+** - 关系型数据库

#### 认证授权
- **JWT (JJWT 0.12.5)** - 基于令牌的认证
- **BCrypt** - 密码哈希算法

#### API文档
- **SpringDoc OpenAPI 2.8.5** - Swagger UI 文档

#### 工具库
- **Apache Commons Lang 3.12.0** - 工具类库
- **Lombok** - 代码简化（可选）

#### 构建工具
- **Maven** - 项目管理和构建
- **Java 17** - JDK 版本

## 📁 项目结构

```
Web_MedicationSalesSystem/
├── src/
│   ├── main/
│   │   ├── java/com/neusoft/coursemgr/
│   │   │   ├── auth/                      # 认证授权模块
│   │   │   │   ├── JwtUtil.java           # JWT工具类
│   │   │   │   ├── AuthInterceptor.java   # 认证拦截器
│   │   │   │   ├── AuthContext.java       # 用户上下文
│   │   │   │   └── AdminGuard.java        # 权限守卫
│   │   │   ├── controller/                # 控制器层
│   │   │   │   ├── UserController.java    # 用户接口
│   │   │   │   ├── ProductController.java # 商品接口
│   │   │   │   ├── CartController.java    # 购物车接口
│   │   │   │   ├── OrderController.java   # 订单接口
│   │   │   │   └── Admin*.java            # 管理端接口
│   │   │   ├── service/                   # 业务逻辑层
│   │   │   │   └── impl/                  # 业务实现
│   │   │   ├── mapper/                    # 数据访问层
│   │   │   ├── domain/                    # 实体类
│   │   │   ├── config/                    # 配置类
│   │   │   ├── exception/                 # 异常处理
│   │   │   └── common/                    # 通用工具
│   │   └── resources/
│   │       ├── application.yml            # 应用配置
│   │       └── mapper/                    # MyBatis XML映射
│   └── test/                              # 单元测试
├── uploads/                               # 文件上传目录
├── pom.xml                                # Maven配置
└── README.md                              # 项目文档
```

## 🚀 快速开始

### 环境要求

- **JDK 17+**
- **MySQL 8.0+**
- **Maven 3.6+**

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/yourusername/Web_MedicationSalesSystem.git
cd Web_MedicationSalesSystem
```

#### 2. 创建数据库

```sql
CREATE DATABASE medicine_sales CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 导入数据表

执行项目中的 SQL 脚本创建数据表（如果提供）。主要数据表包括：

- `user_table` - 用户表
- `product_table` - 药品表
- `category_table` - 分类表
- `order_table` - 订单表
- `order_item_table` - 订单明细表
- `cart_table` - 购物车表
- `cart_item_table` - 购物车条目表
- `review_table` - 评价表
- `banner_table` - 轮播表
- `file_record_table` - 文件记录表

#### 4. 修改配置文件

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medicine_sales?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root           # 修改为你的数据库用户名
    password: your_password  # 修改为你的数据库密码

jwt:
  secret: "your_secret_key_at_least_32_characters_long"  # 修改为你的密钥
  expire-seconds: 86400  # Token过期时间（秒）
```

#### 5. 编译并运行

```bash
# 使用 Maven 编译
mvn clean package

# 运行应用
mvn spring-boot:run

# 或直接运行 JAR 文件
java -jar target/course-mgr-0.0.1-SNAPSHOT.jar
```

#### 6. 访问应用

- **API 基础地址**: http://localhost:8080
- **Swagger 文档**: http://localhost:8080/swagger-ui.html
- **OpenAPI 文档**: http://localhost:8080/v3/api-docs

## 📖 API 文档

### 统一响应格式

所有 API 响应均采用统一的 JSON 格式：

**成功响应**：
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    // 返回的数据
  }
}
```

**失败响应**：
```json
{
  "success": false,
  "message": "错误信息",
  "data": null
}
```

### 主要 API 端点

#### 用户相关
| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | `/api/users/register` | 用户注册 | ❌ |
| POST | `/api/users/login` | 用户登录 | ❌ |
| GET | `/api/users/me` | 获取个人信息 | ✅ |
| PUT | `/api/users/me` | 更新个人信息 | ✅ |

#### 商品相关
| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| GET | `/api/products` | 获取商品列表 | ❌ |
| GET | `/api/products/{id}` | 获取商品详情 | ❌ |
| GET | `/api/categories` | 获取分类列表 | ❌ |

#### 购物车相关
| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| GET | `/api/cart` | 查看购物车 | ✅ |
| POST | `/api/cart/items` | 添加商品到购物车 | ✅ |
| PUT | `/api/cart/items/{id}` | 修改购物车商品数量 | ✅ |
| DELETE | `/api/cart/items/{id}` | 删除购物车商品 | ✅ |
| DELETE | `/api/cart/clear` | 清空购物车 | ✅ |

#### 订单相关
| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | `/api/orders` | 创建订单 | ✅ |
| GET | `/api/orders` | 我的订单列表 | ✅ |
| GET | `/api/orders/{id}` | 订单详情 | ✅ |
| PUT | `/api/orders/{id}/pay` | 支付订单 | ✅ |
| PUT | `/api/orders/{id}/cancel` | 取消订单 | ✅ |
| PUT | `/api/orders/{id}/receive` | 确认收货 | ✅ |

#### 管理端 API
| 方法 | 路径 | 说明 | 需要管理员 |
|------|------|------|-----------|
| POST | `/api/admin/products` | 添加药品 | ✅ |
| PUT | `/api/admin/products/{id}` | 编辑药品 | ✅ |
| DELETE | `/api/admin/products/{id}` | 删除药品 | ✅ |
| GET | `/api/admin/orders` | 所有订单列表 | ✅ |
| PUT | `/api/admin/orders/{id}/ship` | 订单发货 | ✅ |

### 认证方式

所有需要认证的 API 都需要在请求头中携带 JWT Token：

```http
Authorization: Bearer <your_jwt_token>
```

**登录示例**：

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user123",
    "password": "password123"
  }'
```

**响应**：
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "user123",
    "role": "USER"
  }
}
```

**使用 Token 访问受保护的 API**：

```bash
curl -X GET http://localhost:8080/api/cart \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 🔐 认证和授权

### 用户角色

系统支持两种角色：

- **USER** - 普通用户，可以浏览商品、管理购物车、下单等
- **ADMIN** - 管理员，拥有所有权限，可以管理商品、订单、用户等

### JWT 认证流程

```
┌──────────┐                ┌──────────┐                ┌──────────┐
│  客户端  │                │  服务端  │                │  数据库  │
└─────┬────┘                └─────┬────┘                └─────┬────┘
      │                           │                           │
      │  1. POST /login           │                           │
      ├──────────────────────────►│                           │
      │  {username, password}     │  2. 验证用户               │
      │                           ├──────────────────────────►│
      │                           │◄──────────────────────────┤
      │  3. 返回 JWT Token        │  3. 生成 JWT              │
      │◄──────────────────────────┤                           │
      │  {token: "..."}           │                           │
      │                           │                           │
      │  4. 请求受保护资源         │                           │
      ├──────────────────────────►│                           │
      │  Header: Bearer <token>   │  5. 验证 Token            │
      │                           │                           │
      │  6. 返回数据              │                           │
      │◄──────────────────────────┤                           │
```

### 公开 API（无需认证）

- 用户注册和登录
- 商品列表和详情
- 商品分类查询
- 轮播图查询
- 文件上传接口
- Swagger 文档

## 💼 核心业务逻辑

### 订单状态流转

```
┌──────────────┐
│ PENDING_PAY  │  待支付（创建订单后）
└──────┬───────┘
       │ pay()
       ▼
┌──────────────┐
│ PENDING_SHIP │  待发货（支付后）
└──────┬───────┘
       │ ship()
       ▼
┌──────────────────┐
│ PENDING_RECEIVE  │  待收货（发货后）
└──────┬───────────┘
       │ receive()
       ▼
┌──────────────┐
│  COMPLETED   │  已完成（收货后）
└──────────────┘

     或

┌──────────────┐
│ PENDING_PAY  │
└──────┬───────┘
       │ cancel()
       ▼
┌──────────────┐
│  CANCELED    │  已取消
└──────────────┘
```

### 库存管理

#### 下单时扣减库存

```java
// 原子操作，防止超卖
UPDATE product_table
SET stock = stock - ?
WHERE id = ? AND stock >= ? AND status = 1
```

#### 取消订单恢复库存

```java
// 订单取消时恢复库存
UPDATE product_table
SET stock = stock + ?
WHERE id = ?
```

### 购物车逻辑

- 每个用户对应一个购物车（一对一关系）
- 首次添加商品时自动创建购物车
- 添加商品时检查库存是否充足
- 支持修改商品数量、删除商品、清空购物车
- 下单后自动清空购物车

## 🗂️ 数据库设计

### 主要数据表

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| `user_table` | 用户表 | id, username, password_hash, role, status |
| `product_table` | 药品表 | id, name, category_id, price, stock, is_prescription |
| `category_table` | 分类表 | id, name, status, sort_order |
| `order_table` | 订单表 | id, order_no, user_id, status, total_amount |
| `order_item_table` | 订单明细 | id, order_id, product_id, quantity, unit_price |
| `cart_table` | 购物车 | id, user_id |
| `cart_item_table` | 购物车条目 | id, cart_id, product_id, quantity |
| `review_table` | 评价表 | id, user_id, product_id, order_id, rating |
| `banner_table` | 轮播图 | id, title, image_url, status, sort_order |

### 表关系图

```
user_table ──┬── cart_table ─── cart_item_table ─── product_table
             │                                          │
             ├── order_table ─── order_item_table ──────┤
             │                                          │
             └── review_table ──────────────────────────┘
                                                        │
                                 category_table ────────┘
```

## 🛠️ 开发指南

### 添加新的 API 端点

1. **创建实体类** (`domain/`)
2. **创建 Mapper 接口** (`mapper/`)
3. **创建 MyBatis XML** (`resources/mapper/`)
4. **创建 Service 接口和实现** (`service/` 和 `service/impl/`)
5. **创建 Controller** (`controller/`)
6. **添加 Swagger 注解** (`@Operation`, `@Tag` 等)

### 添加权限保护

使用 `AdminGuard` 工具类：

```java
@GetMapping("/admin/users")
public ApiResponse<List<User>> listUsers() {
    // 检查是否为管理员
    AdminGuard.requireAdminUserId();

    // 业务逻辑
    return ApiResponse.success(userService.listUsers());
}
```

### 自定义异常处理

```java
// 抛出业务异常
throw new BizException("商品库存不足");

// 全局异常处理器会自动捕获并返回统一格式
```

### 事务管理

在 Service 方法上添加 `@Transactional` 注解：

```java
@Transactional
public Order createOrder(CreateOrderRequest request) {
    // 扣减库存、创建订单、清空购物车等操作
    // 任一步骤失败都会回滚
}
```

## 📝 配置说明

### application.yml 主要配置项

```yaml
# 服务器端口
server:
  port: 8080

# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medicine_sales
    username: root
    password: root

# JWT 配置
jwt:
  secret: "your_secret_key"      # 至少32位
  expire-seconds: 86400          # Token有效期

# MyBatis 配置
mybatis:
  mapper-locations: classpath:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true  # 下划线转驼峰
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志
```

## 🧪 测试

### 运行单元测试

```bash
mvn test
```

### API 测试

推荐使用以下工具：
- **Swagger UI** - http://localhost:8080/swagger-ui.html
- **Postman** - 导入 API 集合
- **cURL** - 命令行测试

## 📦 部署

### 本地打包运行

```bash
mvn clean package -DskipTests
java -jar target/course-mgr-0.0.1-SNAPSHOT.jar
```

生成的 JAR 文件位于 `target/course-mgr-0.0.1-SNAPSHOT.jar`

### CI/CD 自动部署（GitHub Actions）

项目已配置 GitHub Actions 自动部署流程，推送到 `main` 分支时自动触发：

```
代码推送到 main → Maven 打包 → SCP 上传 JAR → SSH 重启服务
```

#### 工作流步骤（`.github/workflows/deploy.yml`）

| 步骤 | 说明 |
|------|------|
| 拉取代码 | `actions/checkout@v3` |
| 安装 Java 17 | `actions/setup-java@v3`（Temurin 发行版） |
| Maven 打包 | `mvn clean package -DskipTests` |
| 上传 JAR | 通过 `appleboy/scp-action` 将 JAR 上传到服务器 `/opt/app/` |
| 重启服务 | 通过 `appleboy/ssh-action` SSH 到服务器执行重启脚本 |

#### 重启脚本逻辑

```bash
pkill -f course-mgr || true   # 停止旧进程（不存在时忽略）
sleep 3                        # 等待进程退出
nohup java -jar /opt/app/course-mgr-0.0.1-SNAPSHOT.jar > /opt/app/app.log 2>&1 &
sleep 5                        # 等待 JVM 初始化
kill -0 $! && echo "部署成功" || (echo "启动失败" && exit 1)
```

#### 配置所需的 GitHub Secrets

在仓库 Settings → Secrets and variables → Actions 中添加：

| Secret 名称 | 说明 |
|-------------|------|
| `SERVER_HOST` | 服务器 IP 或域名 |
| `SERVER_USER` | SSH 登录用户名 |
| `SERVER_SSH_KEY` | SSH 私钥（PEM 格式） |

### Docker 部署（可选）

创建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/course-mgr-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建和运行：

```bash
docker build -t medication-sales-system .
docker run -p 8080:8080 medication-sales-system
```

## 🔧 常见问题

### 1. 启动时数据库连接失败

检查 `application.yml` 中的数据库配置是否正确，确保 MySQL 服务已启动。

### 2. JWT Token 无效

确保：
- Token 格式正确（`Bearer <token>`）
- Token 未过期
- `jwt.secret` 配置正确

### 3. 跨域问题

项目已配置 CORS，如需修改允许的域名，编辑 `config/CorsConfig.java`。

### 4. 文件上传失败

确保 `uploads/` 目录存在且有写入权限。

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📧 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件至：[your-email@example.com]

## 🙏 致谢

感谢所有为本项目做出贡献的开发者！

---

**注意**：本项目仅用于学习和研究目的，请勿用于实际的医药销售业务。实际医药销售需要遵守相关法律法规并获得相应资质。
