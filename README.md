# 药品销售管理系统

基于 Spring Boot 3 + MyBatis 的药品进销存后端系统，提供药品档案管理、库存批次追踪、出入库记录、财务统计、库存同步等完整业务功能。

## 功能模块

| 模块 | 接口前缀 | 说明 |
|------|----------|------|
| 用户认证 | `/api/users` | 登录、获取/更新当前用户信息、JWT 鉴权 |
| 药品管理 | `/api/drugs` | CRUD、Excel 导入/导出 |
| 供应商管理 | `/api/suppliers` | CRUD、关键词搜索、下拉列表 |
| 入库管理 | `/api/stock/in` | 入库单记录与查询、批次查询、预警查询 |
| 出库管理 | `/api/stock/out` | 出库单记录与查询 |
| 库存同步 | `/api/stock/sync` | Excel 批量同步（预览 + 确认） |
| 数据看板 | `/api/dashboard` | 库存/销售汇总指标 |
| 财务统计 | `/api/finance` | 每日财务明细与汇总、手动触发同步 |
| 文件管理 | `/api/files` | 文件访问（公开） |
| 管理员文件管理 | `/api/admin/files` | 图片上传、文件删除（需管理员权限） |

## 技术栈

- **运行时**：Java 17、Spring Boot 3.4
- **持久层**：MyBatis 3、MySQL 8.0+
- **认证**：JWT（JJWT 0.12）
- **Excel 处理**：EasyExcel（导出）+ Apache POI（导入，原生处理 GBK 编码的 .xls 文件）
- **文件存储**：数据库 BLOB（`file_record_table`）
- **API 文档**：SpringDoc OpenAPI（Swagger UI）
- **监控**：Sentry
- **构建**：Maven、Java 17

## 项目结构

```
src/main/java/com/neusoft/coursemgr/
├── auth/                   # JWT 工具、认证拦截器、用户上下文
├── common/                 # ApiResponse、PageResult
├── config/                 # CORS、拦截器注册
├── controller/             # 各业务 Controller
├── domain/                 # 实体类、VO、Request 类
├── exception/              # BizException、全局异常处理
├── mapper/                 # MyBatis Mapper 接口
├── scheduler/              # 定时任务（每日财务同步）
└── service/                # Service 接口及 impl/

src/main/resources/
├── application.yml
└── mapper/                 # MyBatis XML 映射文件
```

## 数据库表

| 表名 | 说明 |
|------|------|
| `user_table` | 用户（含角色、密码哈希） |
| `drug_table` | 药品档案（编码、名称、价格、库存下限等） |
| `stock_batch_table` | 库存批次（批号、数量、效期、成本价） |
| `stock_in_table` | 入库流水 |
| `stock_out_table` | 出库流水 |
| `supplier_table` | 供应商 |
| `finance_daily_table` | 每日财务汇总 |
| `file_record_table` | 上传文件记录（含 BLOB 数据） |

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 1. 创建数据库

```sql
CREATE DATABASE medicine_sales CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 配置 application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medicine_sales?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password

jwt:
  secret: "至少32位的密钥字符串"
  expire-seconds: 86400
```

### 3. 启动

```bash
mvn spring-boot:run
```

- API 地址：`http://localhost:8080`
- Swagger UI：`http://localhost:8080/swagger-ui.html`

## API 概览

### 统一响应格式

```json
{ "code": 200, "message": "success", "data": { ... } }
```

分页响应的 `data` 结构：

```json
{ "total": 100, "list": [ ... ] }
```

### 认证

需要认证的接口在请求头中携带 Token：

```
Authorization: Bearer <jwt_token>
```

登录接口返回 token：

```bash
POST /api/users/login
{"username": "admin", "password": "123456"}
```

### 核心接口

#### 用户

```
POST /api/users/login       登录，返回 JWT token
GET  /api/users/me          获取当前登录用户信息
PUT  /api/users/me          更新当前用户信息（昵称、密码等）
```

#### 药品管理

```
GET    /api/drugs              分页查询（keyword / category / status / page / size）
GET    /api/drugs/{id}         查询单个
POST   /api/drugs              新增
PUT    /api/drugs/{id}         更新
DELETE /api/drugs/{id}         软删除（status → 0）
GET    /api/drugs/export       导出 Excel（支持 keyword / category / status 过滤）
POST   /api/drugs/import       导入 Excel（.xls/.xlsx，按列名匹配，自动识别格式）
```

Excel 导入支持两种格式（按表头自动判断）：

| 格式 | 识别条件 | 处理逻辑 |
|------|----------|----------|
| 药品档案表 | 不含"有效期"列 | 按编码新增/更新药品，可同时创建初始批次 |
| 效期批次表 | 含"有效期"列 | 回填已有"初始批次"的效期信息，或新建批次 |

#### 供应商管理

```
GET    /api/suppliers           分页查询（keyword / status / page / size）
GET    /api/suppliers/all       查询全部正常状态的供应商（用于下拉选择）
GET    /api/suppliers/{id}      查询单个
POST   /api/suppliers           新增
PUT    /api/suppliers/{id}      更新
DELETE /api/suppliers/{id}      软删除
```

#### 入库管理

```
POST /api/stock/in                    手动入库
GET  /api/stock/in                    分页查询入库记录（drugId / supplierId / startDate / endDate）
GET  /api/stock/batches/{drugId}      查询某药品的所有批次
GET  /api/stock/expire?days=90        查询即将过期批次（默认 90 天内）
GET  /api/stock/low                   查询库存不足批次（库存 < 药品设定的 stockMin）
```

#### 出库管理

```
POST /api/stock/out     手动出库
GET  /api/stock/out     分页查询出库记录（drugId / outType / startDate / endDate）
```

#### 库存同步

```
POST /api/stock/sync/preview   预览同步结果（不写库）
POST /api/stock/sync/confirm   执行同步（写库，事务）
```

两个接口均接收 `multipart/form-data`：`file`（Excel 文件）+ `syncDate`（yyyy-MM-dd）。

同步逻辑对每条药品的变化类型：

| changeType | 含义 | confirm 操作 |
|------------|------|--------------|
| NEW | 数据库中不存在 | 新建完整药品档案 + 初始批次 |
| IN | 新库存 > 当前库存 | 更新批次数量 + 写入库流水 |
| OUT | 新库存 < 当前库存 | 更新批次数量 + 写出库流水 |
| UNCHANGED | 库存相同 | 跳过 |

#### 数据看板

```
GET /api/dashboard
```

返回：药品总数、总库存、90天内到期批次数、库存不足批次数、今日/本月销售额与利润、最近7天财务数据。

#### 财务统计

```
GET  /api/finance/summary      财务汇总（startDate / endDate，默认本月）
GET  /api/finance/daily        每日财务明细列表（startDate / endDate，默认本月）
POST /api/finance/sync         手动触发同步今日财务数据
```

财务数据由定时任务每日自动汇总，也可通过 `/sync` 手动触发。

#### 文件管理

```
GET    /api/files/{id}          获取文件内容（图片直接展示，带长期缓存头）
POST   /api/admin/files/images  管理员上传图片（multipart/form-data，需管理员权限）
DELETE /api/admin/files         管理员删除文件（传 {"url": "/api/files/{id}"}）
```

## Excel 导入说明

- `.xls` 文件：使用 Apache POI `HSSFWorkbook` 直接读取，正确处理 GBK 编码（不依赖 JVM 默认字符集）
- `.xlsx` 文件：使用 `XSSFWorkbook` 读取
- 列名匹配，与列顺序无关
- 药品编码为纯数字时自动补零至 6 位（防止 Excel 丢失前导零）

## 打包部署

```bash
mvn clean package -DskipTests
java -jar target/course-mgr-0.0.1-SNAPSHOT.jar
```
