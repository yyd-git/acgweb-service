**微服务开发与实践课程报告**

**项目名称**：ACG产品展示与管理系统  
**学生姓名**：\[填写姓名\]  
**日期**：2025年12月22日

## 一、项目概述

### 1.1项目背景

本项目是《微服务开发与实践》课程的期末大作业，旨在综合运用课程所学的微服务架构设计、开发、部署和运维知识，设计并实现一个完整的微服务应用系统。

### 1.2项目名称

ACG产品展示与管理系统

### 1.3项目类型

在线内容管理与展示系统（动漫 / 漫画 / 游戏产品管理）

**项目目标**

- 提供完整的ACG产品信息管理与展示功能，包括添加、修改、删除和展示产品详情
- 实现分布式微服务架构，支持服务注册与发现、API网关、负载均衡
- 支持容器化部署和自动化运维，便于快速上线和扩展

## 二、功能特性

### 2.1核心功能列表

1.  **用户管理模块**
    - 用户注册、登录
    - 用户信息修改与查询
    - 权限管理（管理员 / 普通用户）
2.  **产品管理模块**
    - 产品信息的增删改查
    - 按类型（动漫/漫画/轻小说/游戏）分类管理
    - 产品详情展示
3.  **文件管理模块**
    - 产品封面和资源文件上传
    - 文件大小限制（最大200MB）
    - 存储路径可配置，本地Windows路径为 app-data

## 三、技术栈说明

### 3.1核心技术框架

| **组件分类** | **技术选型** | **版本** | **用途说明** |
| --- | --- | --- | --- |
| 开发语言 | Java | 17  | 主要编程语言 |
| 应用框架 | Spring Boot | 3.3.3 | 微服务应用开发框架 |
| 微服务框架 | Spring Cloud | 2023.0.3 | 微服务治理与服务间通信 |
| 阿里云组件 | Spring Cloud Alibaba | 2023.0.1.0 | Nacos注册中心与配置中心 |
| 数据库 | MySQL | 8.0+ | 数据持久化 |
| 服务注册与发现 | Nacos | v3.1.0 | 注册中心和配置中心 |
| API网关 | Spring Cloud Gateway | 随Spring Cloud | 请求路由和统一入口 |
| 服务间通信 | OpenFeign | 随Spring Cloud | 声明式HTTP客户端 |
| 负载均衡 | Spring Cloud LoadBalancer | 随Spring Cloud | 客户端负载均衡 |
| 容错保护 | Resilience4j | 随Spring Cloud | 熔断、限流、重试 |
| 日志与监控 | Spring Boot Actuator | 随Spring Boot | 健康检查与指标暴露 |

### 3.2可选技术组件

消息队列：RabbitMQ（可选）

分布式事务：Seata（可选）

缓存：Redis（可选）

认证授权：JWT（jjwt 0.11.5）+ Spring Security

链路追踪：Sleuth + Zipkin（可选）

服务监控：Prometheus + Grafana（可选）

日志聚合：ELK/EFK Stack（可选）

### 3.3容器与编排

| **组件** | **版本** | **用途** |
| --- | --- | --- |
| Docker | 27.0+ | 容器运行时 |
| Docker Compose | 2.29+ | 本地开发环境编排 |

## 四、系统架构设计

### 4.1整体服务架构设计

本项目采用 **基于 Spring Cloud 的微服务架构**，系统由 **API 网关、多个业务微服务、服务注册中心（Nacos）** 以及前端应用共同组成。  
所有客户端请求统一通过 **Gateway 网关服务** 进入系统，由网关完成路由转发、跨域处理与统一鉴权控制，各业务微服务之间通过服务名进行解耦通信。

整体架构遵循 **“统一入口、服务解耦、按业务拆分”** 的设计原则，提升系统的可维护性、扩展性与安全性。

#### 架构核心组件说明

1\. API 网关（Gateway Service）

- 服务名称：gateway-service
- 端口号：8090
- 技术选型：Spring Cloud Gateway
- 注册中心：Nacos

**主要职责：**

- 作为系统的统一访问入口
- 根据请求路径将请求路由到对应微服务
- 统一处理跨域（CORS）配置
- 承载 JWT 鉴权逻辑，支持白名单接口
- 屏蔽内部服务结构，增强系统安全性

**跨域支持：**

- 允许来自本地开发环境（如 localhost:5173、localhost:8080 等）的前端访问
- 支持所有 HTTP 方法与请求头
- 支持携带 Cookie 和 Token

2\. 服务注册与发现（Nacos）

- 所有微服务与网关统一注册到 Nacos
- Gateway 通过 lb://服务名 的方式进行负载均衡访问
- 微服务实例上下线无需修改配置，提升系统弹性

#### 微服务划分与职责

根据当前 Gateway 路由配置，系统包含以下核心微服务：

| **服务名称** | **服务说明** | **访问入口（Gateway 路由）** |
| --- | --- | --- |
| user-service | 用户管理与认证服务 | /user/\*\* |
| acg-product-service | ACG 产品管理与资源服务 | /acg-product/\*\* |
| acg-rating-service | 产品评分服务 | /acg-rating/\*\* |
| acg-comment-service | 产品评论服务 | /acg-comment/\*\* |
|     |     |     |

**注：**acg-rating-service和acg-comment-service由于功能相近，合并在acg-rating-service服务中未做拆分

**网关路由设计说明**

API 网关通过**显式路由配置**的方式对外提供服务转发，未启用自动服务发现路由（discovery.locator.enabled=false），避免路径混乱，增强可控性。

**核心路由配置如下：**

| **路由ID** | **目标服务** | **路径匹配规则** | **功能说明** |
| --- | --- | --- | --- |
| user-service | user-service | /user/\*\* | 用户注册、登录、信息管理 |
| acg-product-service | acg-product-service | /acg-product/\*\* | 产品信息管理 |
| acg-comment-service | acg-rating-service | /acg-comment/\*\* | 产品评论相关接口 |
| acg-rating-service | acg-rating-service | /acg-rating/\*\* | 产品评分接口 |
| acg-product-cover | acg-product-service | /cover/\*\* | 产品封面文件访问 |
| acg-product-detail-resource | acg-product-service | /resource/\*\* | 产品详情资源访问 |
| acg-product-resource | acg-product-service | /acg-resource/\*\* | 产品资源文件管理 |

说明：  
产品相关的**封面、资源文件**统一由 acg-product-service 提供，但在网关层通过不同路径进行区分，提升接口语义清晰度。

#### 统一鉴权与白名单设计

系统在 **Gateway 层统一处理 JWT 鉴权逻辑**，避免各业务服务重复实现安全校验。

**JWT 配置特点：**

- 使用 HS256 对称加密算法
- Token 有效期：24 小时
- 所有业务请求默认需要携带 Token

**白名单接口：**

以下接口在网关层放行，无需登录即可访问：

- /user/login
- /user/register

该设计实现了：

- 登录注册接口的开放访问
- 业务接口的统一安全控制
- 安全逻辑集中化，降低微服务耦合度

#### 服务调用与请求流程

系统的典型请求流程如下：

1.  前端通过 http://localhost:8090 访问系统
2.  请求首先进入 Gateway 网关
3.  网关进行：
    - 跨域校验
    - JWT 鉴权（白名单除外）
    - 路由匹配
4.  请求通过负载均衡转发到目标微服务
5.  微服务处理业务逻辑并访问自身数据库
6.  返回统一格式的 JSON 响应给前端

### 4.2、数据库设计

#### 数据库设计原则

本项目在微服务架构下，采用 **“服务独立数据库（Database per Service）”** 的设计模式，每个微服务拥有独立的数据存储结构，避免服务间通过数据库耦合，提升系统的可维护性和可扩展性。

数据库设计遵循以下原则：

- **按服务划分数据库**，避免跨库访问
- **采用软删除机制**，保证数据可追溯性
- **统一使用自增主键（BIGINT）**，便于扩展
- **记录创建时间**，方便审计与排序
- **通过唯一约束保证业务一致性**

#### 数据库具体设计

用户服务数据库设计（user-service）

**表：user**

该表用于存储系统用户的基本信息，服务于用户注册、登录和身份认证等功能。

**表结构说明**

| **字段名** | **类型** | **约束** | **说明** |
| --- | --- | --- | --- |
| id  | BIGINT | 主键，自增 | 用户唯一标识 |
| user_name | VARCHAR | NOT NULL，UNIQUE | 用户名，系统唯一 |
| user_password | VARCHAR | NOT NULL | 加密后的用户密码 |
| create_time | DATETIME | NOT NULL | 用户创建时间 |
| is_deleted | BOOLEAN | NOT NULL | 软删除标志 |

**设计说明**

- user_name 设置唯一约束，防止重复注册
- 密码字段仅保存**加密后结果**，不存明文
- 使用 is_deleted 实现软删除，避免直接物理删除用户数据
- create_time 由构造方法自动赋值，保证时间一致性

产品服务数据库设计（acg-product-service）

**表一：acg_product**

该表用于存储 ACG 产品的核心信息，支持动漫、漫画、轻小说和游戏等多种类型。

**表结构说明**

| **字段名** | **类型** | **约束** | **说明** |
| --- | --- | --- | --- |
| id  | BIGINT | 主键，自增 | 产品ID |
| name | VARCHAR | NOT NULL | 产品名称 |
| description | VARCHAR(1000) | 可为空 | 产品简介 |
| type | VARCHAR | NOT NULL | 产品类型（枚举） |
| cover_path | VARCHAR | 可为空 | 封面图片路径 |
| total_score | DOUBLE | NOT NULL | 产品总评分 |
| has_resource | BOOLEAN | NOT NULL | 是否存在资源 |
| studio | VARCHAR | 可为空 | 动画制作公司 |
| episode_count | INT | 可为空 | 动画集数 |
| author | VARCHAR | 可为空 | 作者  |
| chapter_count | INT | 可为空 | 章节数 |
| volume_count | INT | 可为空 | 卷数  |
| developer | VARCHAR | 可为空 | 游戏开发商 |
| platform | VARCHAR | 可为空 | 游戏平台 |
| create_time | DATETIME | NOT NULL | 创建时间 |
| is_deleted | BOOLEAN | NOT NULL | 软删除标志 |

**设计说明**

- 使用 AcgProductType 枚举区分产品类型（ANIME / COMIC / NOVEL / GAME）
- 不同类型产品共用一张表，通过字段是否为空区分
- total_score 用于评分系统聚合结果
- has_resource 用于快速判断产品是否存在可下载资源
- 统一使用软删除，保证历史数据可恢复

**表二：acg_resource**

该表用于存储与 ACG 产品关联的资源信息，如压缩包、附件等。

**表结构说明**

| **字段名** | **类型** | **约束** | **说明** |
| --- | --- | --- | --- |
| id  | BIGINT | 主键，自增 | 资源ID |
| user_id | BIGINT | NOT NULL | 上传用户ID |
| product_id | BIGINT | NOT NULL | 所属产品ID |
| name | VARCHAR | NOT NULL | 资源名称 |
| description | VARCHAR(1000) | 可为空 | 资源描述 |
| resource_path | VARCHAR | NOT NULL | 本地存储路径 |
| create_time | DATETIME | NOT NULL | 创建时间 |
| is_deleted | BOOLEAN | NOT NULL | 软删除标志 |

**设计说明**

- 一个产品可对应多个资源
- 通过 product_id 与产品建立逻辑关联
- resource_path 保存资源在服务器本地的存储路径
- 支持用户上传资源，保留上传者信息

评分与评论服务数据库设计（acg-rating-service）

**表一：acg_rating**

该表用于存储用户对产品的评分信息。

**表结构说明**

| **字段名** | **类型** | **约束** | **说明** |
| --- | --- | --- | --- |
| id  | BIGINT | 主键，自增 | 评分ID |
| user_id | BIGINT | NOT NULL | 用户ID |
| product_id | BIGINT | NOT NULL | 产品ID |
| score | INT | NOT NULL | 评分值（1~10） |
| create_time | DATETIME | NOT NULL | 创建时间 |
| is_deleted | BOOLEAN | NOT NULL | 软删除标志 |

**关键约束设计**

- (user_id, product_id) 设为 **联合唯一约束**
- 保证 **一个用户只能对同一产品评分一次**

**设计说明**

- 支持后续对评分进行更新或撤销
- 数据聚合后可用于更新 acg_product.total_score

**表二：acg_comment**

该表用于存储用户对产品的评论内容。

**表结构说明**

| **字段名** | **类型** | **约束** | **说明** |
| --- | --- | --- | --- |
| id  | BIGINT | 主键，自增 | 评论ID |
| user_id | BIGINT | NOT NULL | 用户ID |
| product_id | BIGINT | NOT NULL | 产品ID |
| content | VARCHAR(1000) | NOT NULL | 评论内容 |
| create_time | DATETIME | NOT NULL | 创建时间 |
| is_deleted | BOOLEAN | NOT NULL | 软删除标志 |

**设计说明**

- 一个用户可对同一产品发表多条评论
- 评论采用软删除，便于后台管理和审计

#### 数据库设计总结

- 各微服务数据库 **相互独立，职责清晰**
- 统一采用 **软删除机制**
- 合理使用 **唯一约束保证业务规则**
- 表结构与业务模型高度一致，便于 ORM 映射
- 支持后续扩展（如点赞、收藏、标签等功能）

### 4.3、API 设计说明

#### RESTful API 设计原则

本系统采用 RESTful 架构风格对后端接口进行设计，以实现前后端解耦和微服务之间的高效通信。API 设计遵循以下原则：

1.  **使用 HTTP 方法表示操作语义**
    - GET：查询资源
    - POST：创建资源或提交操作
    - PUT：更新资源
    - DELETE：删除资源（本系统采用软删除方式）
2.  **使用 URI 表示资源**
    - 不同业务模块通过不同路径区分，如用户、产品、资源、评分、评论等
3.  **接口无状态**
    - 每次请求均包含完整参数，服务端不保存客户端状态
4.  **统一返回结果格式**
    - 所有接口均返回统一的 Result 对象，便于前端处理成功与失败结果
5.  **业务模块解耦**
    - 用户、产品、资源、评分、评论分别由独立服务实现，通过接口调用协作完成业务功能

#### 统一响应结果格式设计

系统中所有接口统一使用 Result 类作为返回值，其定义如下：

public class Result {

private Integer code; // 1 成功，0 失败

private String msg;

private Object data;

}

统一返回 JSON 格式如下：

{

"code": 1,

"msg": "success",

"data": {}

}

字段说明如下：

| **字段名** | **类型** | **说明** |
| --- | --- | --- |
| code | Integer | 状态码，1 表示成功，0 表示失败 |
| msg | String | 提示信息 |
| data | Object | 返回的业务数据，如实体对象、列表或 Token |

#### 用户服务 API 设计（User Service）

**基础路径：** /user

用户注册

| **项目** | **说明** |
| --- | --- |
| 请求方法 | POST |
| 请求路径 | /user/register |
| 请求参数 | username，password |
| 功能说明 | 新用户注册 |

**请求示例：**

POST /user/register

**响应示例：**

{

"code": 1,

"msg": "注册成功",

"data": null

}

用户登录

| **项目** | **说明** |
| --- | --- |
| 请求方法 | POST |
| 请求路径 | /user/login |
| 请求参数 | username，password |
| 功能说明 | 用户登录并返回 JWT Token |

**响应示例：**

{

"code": 1,

"msg": "success",

"data": "eyJhbGciOiJIUzI1NiJ9..."

}

删除用户（软删除）

| **项目** | **说明** |
| --- | --- |
| 请求方法 | DELETE |
| 请求路径 | /user/{id} |
| 请求参数 | 用户 ID |
| 功能说明 | 根据 ID 软删除用户 |

根据 ID 查询用户

| **项目** | **说明** |
| --- | --- |
| 请求方法 | GET |
| 请求路径 | /user/{id} |
| 请求参数 | 用户 ID |
| 功能说明 | 查询用户信息（供 Feign 调用） |

#### ACG 产品服务 API 设计（Product Service）

**基础路径：** /acg-product

新增 ACG 产品（支持封面上传）

| **项目** | **说明** |
| --- | --- |
| 请求方法 | POST |
| 请求路径 | /acg-product |
| 请求类型 | multipart/form-data |
| 功能说明 | 新增动漫 / 漫画 / 小说 / 游戏产品 |

**主要参数：**

| **参数名** | **说明** |
| --- | --- |
| name | 产品名称 |
| description | 产品简介 |
| type | 产品类型（ANIME / COMIC / NOVEL / GAME） |
| coverFile | 封面图片（可选） |

查询所有产品

| **项目** | **说明** |
| --- | --- |
| 请求方法 | GET |
| 请求路径 | /acg-product |
| 功能说明 | 查询全部未删除产品 |

按类型查询产品

| **项目** | **说明** |
| --- | --- |
| 请求方法 | GET |
| 请求路径 | /acg-product/type/{type} |
| 功能说明 | 根据产品类型查询 |

分页查询产品

| **项目** | **说明** |
| --- | --- |
| 请求方法 | GET |
| 请求路径 | /acg-product/page |
| 功能说明 | 支持分页、名称模糊查询和类型筛选 |

删除产品（软删除）

| **项目** | **说明** |
| --- | --- |
| 请求方法 | DELETE |
| 请求路径 | /acg-product/{id} |
| 功能说明 | 根据 ID 软删除产品 |

更新产品评分

| **项目** | **说明** |
| --- | --- |
| 请求方法 | PUT |
| 请求路径 | /acg-product/{id}/score |
| 功能说明 | 由评分服务调用更新产品评分 |

#### ACG 资源服务 API 设计（Resource Service）

**基础路径：** /acg-resource

上传资源文件

| **项目** | **说明** |
| --- | --- |
| 请求方法 | POST |
| 请求路径 | /acg-resource/upload |
| 请求类型 | multipart/form-data |
| 功能说明 | 上传 zip / rar 等资源文件 |

下载资源文件

| **项目** | **说明** |
| --- | --- |
| 请求方法 | GET |
| 请求路径 | /acg-resource/download/{id} |
| 功能说明 | 根据资源 ID 下载文件 |

根据产品查询资源列表

| **项目** | **说明** |
| --- | --- |
| 请求方法 | GET |
| 请求路径 | /acg-resource/list/{productId} |
| 功能说明 | 查询指定产品下的全部资源 |

评分与评论服务 API 设计

**产品评分接口**

**基础路径：** /acg-rating

| **项目** | **说明** |
| --- | --- |
| 请求方法 | POST |
| 请求路径 | /acg-rating/rate |
| 功能说明 | 用户对产品进行评分 |

产品评论接口

**基础路径：** /acg-comment

| **项目** | **说明** |
| --- | --- |
| 请求方法 | POST |
| 请求路径 | /acg-comment/add |
| 功能说明 | 用户发表评论 |

| 请求方法 | GET |  
| 请求路径 | /acg-comment/list/{productId} |  
| 功能说明 | 查询指定产品的评论列表 |

## 五、核心功能实现

### 5.1 阶段一：服务拆分与注册发现

#### 设计思路

本项目采用 **Spring Cloud 微服务架构**，按照业务领域对系统进行服务拆分，每个微服务负责单一且明确的业务功能，从而提高系统的可维护性与扩展性。

系统主要拆分为以下核心服务：

- **user-service（用户服务）**：负责用户注册、登录、用户信息管理
- **acg-product-service（产品服务）**：负责 ACG 产品信息、封面上传、资源管理
- **acg-rating-service（评分与评论服务）**：负责产品评分与评论功能
- **gateway-service（网关服务）**：统一入口，负责路由转发、跨域处理与权限白名单控制

各微服务通过 **Nacos 注册中心**进行统一管理，实现服务的自动注册与发现，避免硬编码服务地址，提高系统的灵活性。

**整体架构特点：**

- 微服务独立部署、独立端口
- 基于服务名进行调用（lb://service-name）
- 服务之间通过 Nacos 实现解耦
- 所有外部请求统一经由网关进入系统

#### Nacos 注册中心部署

**（1）Nacos Docker Compose 部署**

项目中使用 Docker Compose 部署单机版 Nacos，并使用 MySQL 作为配置与元数据存储。

启动命令：

docker-compose up -d

启动完成后，可通过以下地址访问 Nacos 控制台：

http://localhost:8848/nacos

默认账号密码为 nacos / nacos。

#### 微服务注册到 Nacos

各微服务均引入以下依赖以支持服务注册与发现：

&lt;dependency&gt;

&lt;groupId&gt;com.alibaba.cloud&lt;/groupId&gt;

&lt;artifactId&gt;spring-cloud-starter-alibaba-nacos-discovery&lt;/artifactId&gt;

&lt;/dependency&gt;

**（1）user-service 注册配置（8081）**

server:

port: 8081

spring:

application:

name: user-service

cloud:

nacos:

discovery:

server-addr: localhost:8848

heart-beat-interval: 5000

heart-beat-timeout: 20000

ephemeral: true

**（2）acg-product-service 注册配置（8082）**

server:

port: 8082

spring:

application:

name: acg-product-service

cloud:

nacos:

discovery:

server-addr: localhost:8848

heart-beat-interval: 5000

heart-beat-timeout: 20000

ephemeral: true

**（3）acg-rating-service 注册配置（8083）**

server:

port: 8083

spring:

application:

name: acg-rating-service

cloud:

nacos:

discovery:

server-addr: localhost:8848

heart-beat-interval: 5000

heart-beat-timeout: 20000

ephemeral: true

**（4）gateway-service 注册配置（8090）**

server:

port: 8090

spring:

application:

name: gateway-service

cloud:

nacos:

discovery:

server-addr: 127.0.0.1:8848

#### 服务注册验证

启动所有微服务后，登录 Nacos 控制台，在 **“服务管理 → 服务列表”** 中可以看到：

| **服务名** | **实例数** | **健康实例数** |
| --- | --- | --- |
| user-service | 1   | 1   |
| acg-product-service | 1   | 1   |
| acg-rating-service | 1   | 1   |
| gateway-service | 1   | 1   |

说明所有服务均已成功注册，并处于健康状态。

### 5.2 阶段二：服务间通信与负载均衡

#### 网关统一入口设计

本项目采用 **Spring Cloud Gateway** 作为统一访问入口，所有前端请求均通过网关转发至后端具体服务。

**网关路由配置示例：**

spring:

cloud:

gateway:

routes:

\- id: user-service

uri: lb://user-service

predicates:

\- Path=/user/\*\*

\- id: acg-product-service

uri: lb://acg-product-service

predicates:

\- Path=/acg-product/\*\*

\- id: acg-rating-service

uri: lb://acg-rating-service

predicates:

\- Path=/acg-rating/\*\*

其中 lb:// 表示通过 **Nacos + Spring Cloud LoadBalancer** 进行负载均衡调用。

#### 跨域与白名单配置

网关统一配置跨域策略，解决前后端分离开发中的跨域问题：

globalcors:

cors-configurations:

'\[/\*\*\]':

allowedOriginPatterns:

\- http://localhost:5173

allowedMethods: "\*"

allowedHeaders: "\*"

allowCredentials: true

同时，针对登录与注册接口设置 JWT 白名单：

jwt:

whitelist:

\- /user/login

\- /user/register

#### Feign 服务调用与负载均衡

项目中引入 **OpenFeign** 实现声明式服务调用，简化服务间通信。

&lt;dependency&gt;

&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;

&lt;artifactId&gt;spring-cloud-starter-openfeign&lt;/artifactId&gt;

&lt;/dependency&gt;

通过 Feign，服务之间只需基于服务名进行调用，无需关心具体 IP 与端口信息，负载均衡由 Spring Cloud LoadBalancer 自动完成。

#### 熔断与容错机制（Resilience4j）

为提升系统稳定性，项目集成 **Resilience4j** 作为熔断与容错组件。

resilience4j:

circuitbreaker:

instances:

catalogService:

slidingWindowSize: 10

minimumNumberOfCalls: 5

failureRateThreshold: 50

waitDurationInOpenState: 5s

当服务调用失败率超过阈值时，系统将自动触发熔断，避免级联故障，提高系统整体可用性。

### 5.3 阶段三：API 网关与统一认证设计

#### 设计背景与总体思路

在微服务架构下，系统包含用户服务、ACG 产品服务、评分服务、评论服务等多个独立服务。如果客户端直接访问各个微服务，不仅接口分散，而且安全控制、跨域处理、认证逻辑将大量重复，系统整体复杂度和维护成本较高。

因此，本系统在微服务架构之上引入 **API 网关（Gateway）** 作为统一入口，集中处理请求转发与通用功能，实现 **“对外统一、对内解耦”** 的系统架构设计。

本阶段采用 **Spring Cloud Gateway + Nacos 服务发现 + JWT 认证机制**，构建统一的 API 网关与认证体系。

#### API Gateway 的职责划分

在本系统中，Gateway 服务承担以下核心职责：

1.  **统一入口管理**
    - 所有前端请求均通过 Gateway 进入系统
    - 客户端无需感知后端微服务的具体地址与端口
2.  **路由转发**
    - 根据请求路径，将请求转发至对应的微服务
    - 结合 Nacos 实现基于服务名的负载均衡调用（lb://）
3.  **统一身份认证**
    - 在 Gateway 层完成 JWT 令牌校验
    - 避免各微服务重复实现认证逻辑
4.  **用户信息透传**
    - 将解析后的用户 ID、用户名注入请求头
    - 下游服务可直接获取用户身份信息
5.  **跨域统一处理**
    - 在网关层集中配置 CORS，避免多服务重复配置
6.  **安全防护**
    - 未登录请求拦截
    - 非法 Token 直接拒绝访问

#### Gateway 服务配置设计

Gateway 基础配置

Gateway 服务端口配置为 **8090**，并注册至 Nacos 服务中心：

server:

port: 8090

spring:

application:

name: gateway-service

cloud:

nacos:

discovery:

server-addr: 127.0.0.1:8848

通过 Nacos 实现服务注册与发现，Gateway 可动态感知后端服务实例变化。

路由规则设计

Gateway 使用 **基于路径的路由策略**，将不同前缀的请求转发至对应微服务：

| **路径前缀** | **目标服务** |
| --- | --- |
| /user/\*\* | user-service |
| /acg-product/\*\* | acg-product-service |
| /acg-rating/\*\* | acg-rating-service |
| /acg-comment/\*\* | acg-rating-service |
| /acg-resource/\*\* | acg-product-service |
| /cover/\*\* | acg-product-service |
| /resource/\*\* | acg-product-service |

示例配置如下：

spring:

cloud:

gateway:

routes:

\- id: user-service

uri: lb://user-service

predicates:

\- Path=/user/\*\*

\- id: acg-product-service

uri: lb://acg-product-service

predicates:

\- Path=/acg-product/\*\*

通过 lb://服务名 的方式，Gateway 能够自动从 Nacos 中选择可用实例，实现负载均衡调用。

#### JWT 统一认证机制设计

认证模式说明

本系统采用 **JWT（JSON Web Token）无状态认证机制**，认证流程如下：

1.  用户在 **user-service** 中完成登录
2.  登录成功后由 user-service 生成 JWT
3.  前端将 JWT 存储并在后续请求中携带
4.  Gateway 在请求入口统一校验 JWT
5.  校验通过后，请求转发至目标服务

**Gateway 不负责生成 Token，仅负责校验与解析 Token。**

JWT 工具类设计（Gateway 侧）

Gateway 中的 JwtUtil 主要用于 Token 的解析与校验：

- 使用 HS256 对称加密算法
- 从配置文件中读取密钥
- 通过 @PostConstruct 初始化签名 Key

核心功能包括：

- 校验 Token 合法性
- 解析 Claims
- 获取 username 与 userId

该设计确保 Gateway 能安全、统一地完成身份解析。

**5.4.3 JWT 全局过滤器设计**

Gateway 使用 **WebFilter** 实现统一认证拦截，核心流程如下：

1.  **白名单路径放行**
    - /user/login
    - /user/register
2.  **静态资源放行**
    - /cover/\*\*
    - /resource/\*\*
    - /images/\*\*
    - /uploads/\*\*

1.  **OPTIONS 请求放行**
    - 用于支持浏览器预检请求

1.  **JWT 校验**
    - 从 Authorization 请求头中读取 Token
    - 校验 Token 是否合法
    - 非法请求直接返回 401 Unauthorized

1.  **用户信息透传**
    - 从 Token 中解析 userId 与 username
    - 注入到请求头中：
        - X-User-Id
        - X-Username

该过滤器在 Gateway 层完成所有认证逻辑，下游服务无需关心 Token 校验细节。

#### 跨域（CORS）统一处理

为解决前后端分离架构中的跨域问题，系统在 Gateway 层统一配置 CORS：

gateway:

globalcors:

cors-configurations:

'\[/\*\*\]':

allowedOriginPatterns:

\- http://localhost:5173

\- http://localhost:8080

allowedMethods: "\*"

allowedHeaders: "\*"

allowCredentials: true

该方式避免了在每个微服务中重复配置跨域规则，降低维护成本。

#### 认证流程说明

系统整体认证流程如下：

1.  用户访问登录接口 /user/login
2.  user-service 校验用户信息并生成 JWT
3.  前端在请求头中携带 Authorization: Bearer &lt;token&gt;
4.  Gateway 拦截请求并校验 JWT
5.  校验成功后，将用户信息透传至目标微服务
6.  目标微服务执行业务逻辑并返回结果

#### 本章小结

本章围绕 **API 网关与统一认证** 进行了系统设计与实现说明，主要完成了以下工作：

- 构建基于 Spring Cloud Gateway 的统一入口
- 结合 Nacos 实现微服务路由与负载均衡
- 采用 JWT 实现无状态统一认证
- 在 Gateway 层完成身份校验与用户信息透传
- 实现统一跨域与安全拦截机制

该设计有效提升了系统的安全性、可维护性和扩展性，为后续微服务功能扩展提供了稳定的基础架构支持。

### 5.4 阶段四：配置中心

#### 设计思路

使用Nacos Config作为配置中心，实现配置的集中管理和动态刷新，支持多环境配置隔离。

配置中心优势：

• 集中管理：所有配置统一存储在Nacos

• 动态刷新：配置变更无需重启服务

• 环境隔离：dev/test/prod环境配置分离

• 版本管理：配置历史版本回滚

• 安全性：敏感配置加密存储

#### 实现细节

Nacos Config集成

添加依赖：

&lt;dependency&gt;

&lt;groupId&gt;com.alibaba.cloud&lt;/groupId&gt;

&lt;artifactId&gt;spring-cloud-starter-alibaba-nacos-config&lt;/artifactId&gt;

&lt;/dependency&gt;

&lt;dependency&gt;

&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;

&lt;artifactId&gt;spring-cloud-starter-bootstrap&lt;/artifactId&gt;

&lt;/dependency&gt;

application配置（application.yml）：

spring:  
application:  
name: user-service  
<br/>cloud:  
nacos:  
config:  
file-extension: yaml  
group: DEFAULT_GROUP  
namespace: public  
<br/>spring.config.import: nacos:${spring.application.name}.yaml

用application-local.yml和application-docker.yml做环境隔离

Local：

  
spring:  
cloud:  
nacos:  
server-addr: 127.0.0.1:8848

docker：

spring:  
cloud:  
nacos:  
server-addr: nacos:8848

  

在Nacos中创建配置：以user-service为示例

登录Nacos控制台：http://localhost:8848/nacos

创建配置：

• Data ID：user-service.yaml

• Group：DEFAULT_GROUP

• 配置格式：YAML

• 配置内容：

server:

port: 8081

spring:

application:

name: user-service

datasource:

url: jdbc:mysql://localhost:3306/acg_user_db?useSSL=false&serverTimezone=Asia/Shanghai

username: root

password: "00000712"

jpa:

hibernate:

ddl-auto: update

show-sql: true

management:

endpoints:

web:

exposure:

include: health,info

jwt:

secret: "MySuperSecureJwtSecretKeyForHS256_123456"

expiration: 86400000 # 24小时

### 5.5 阶段五：容器化部署

#### 多环境配置管理

创建不同环境的配置：

1\. 开发环境

o Data ID: user-service.yaml

o 数据库jdbc:mysql://localhost:3306/acg_user_db?useSSL=false&serverTimezone=Asia/Shanghai

3\. 生产环境

o Data ID: user-service.yaml

o 数据库: jdbc:mysql://localhost:3306/acg_user_db?useSSL=false&serverTimezone=Asia/Shanghai

启动时指定环境：

\# 开发环境

java -jar user-service.jar --spring.profiles.active=docker

\# 生产环境

java -jar user-service.jar --spring.profiles.active=docker

### 5.6 阶段六：前端编写

使用vue脚手架构建前端代码

路由设置

Jwt解析

Api请求拼接类

Login页面

Register页面

Productlistvue

Productvue

#### 实现细节

Dockerfile 编写（以微服务 Rating Service 为例）

采用 **多阶段构建**：

1.  **构建阶段**：使用 Maven 镜像下载依赖并编译项目。
2.  **运行阶段**：使用轻量 JRE 镜像运行 Jar 包，创建非 root 用户以提升安全性。

\# ==============================

\# Stage 1: Build the application

\# ==============================

FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

\# ==============================

\# Stage 2: Run the application

\# ==============================

FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /app/target/\*.jar /app/app.jar

EXPOSE 8083

USER appuser

ENTRYPOINT \["java", "-jar", "app.jar"\]

说明：其他微服务 Dockerfile 模板类似，只需修改 EXPOSE 端口与服务名称。

Docker Compose 编排

根据实际部署情况设计如下 docker-compose.yml：

- **Nacos**：配置中心 + 服务注册中心
- **MySQL**：存储微服务数据
- **微服务**：user-service、acg-product-service、acg-rating-service、gateway-service
- **前端**：Nginx 容器

version: "3.8"

services:

nacos:

image: nacos/nacos-server:v2.3.2

container_name: nacos

environment:

MODE: standalone

PREFER_HOST_MODE: hostname

NACOS_AUTH_ENABLE: "false"

TZ: Asia/Shanghai

ports:

\- "8848:8848"

\- "9848:9848"

volumes:

\- ./nacos-data:/home/nacos/data

networks:

\- acg-demo-network

restart: always

healthcheck:

test: \["CMD-SHELL", "curl -s http://localhost:8848/nacos/v1/ns/operator/servers || exit 0"\]

interval: 10s

timeout: 5s

retries: 5

start_period: 90s

mysql:

image: mysql:8.0

container_name: acg-demo-mysql

environment:

MYSQL_ROOT_PASSWORD: "00000712"

MYSQL_DATABASE: acg_demo

MYSQL_USER: acg

MYSQL_PASSWORD: "acg123456"

TZ: Asia/Shanghai

ports:

\- "3306:3306"

volumes:

\- mysql_data:/var/lib/mysql

\- ./mysql-init:/docker-entrypoint-initdb.d

command:

\--character-set-server=utf8mb4

\--collation-server=utf8mb4_unicode_ci

\--default-authentication-plugin=mysql_native_password

networks:

\- acg-demo-network

restart: always

healthcheck:

test: \["CMD", "mysqladmin", "ping", "-h", "localhost"\]

interval: 5s

timeout: 5s

retries: 10

start_period: 20s

user-service:

build:

context: ./user-service

container_name: user-service

ports:

\- "8081:8081"

environment:

SPRING_PROFILES_ACTIVE: docker

SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR: nacos:8848

SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR: nacos:8848

depends_on:

nacos:

condition: service_healthy

mysql:

condition: service_healthy

networks:

\- acg-demo-network

restart: always

acg-product-service:

build:

context: ./acg-product-service

container_name: acg-product-service

ports:

\- "8082:8082"

environment:

SPRING_PROFILES_ACTIVE: docker

SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR: nacos:8848

SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR: nacos:8848

volumes:

\- ./acg-product-data:/app/data

depends_on:

nacos:

condition: service_healthy

mysql:

condition: service_healthy

networks:

\- acg-demo-network

restart: always

acg-rating-service:

build:

context: ./acg-rating-service

container_name: acg-rating-service

ports:

\- "8083:8083"

environment:

SPRING_PROFILES_ACTIVE: docker

SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR: nacos:8848

SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR: nacos:8848

depends_on:

nacos:

condition: service_healthy

mysql:

condition: service_healthy

acg-product-service:

condition: service_started

networks:

\- acg-demo-network

restart: always

gateway-service:

build:

context: ./gateway-service

container_name: gateway-service

ports:

\- "8090:8090"

environment:

SPRING_PROFILES_ACTIVE: docker

SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR: nacos:8848

SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR: nacos:8848

depends_on:

nacos:

condition: service_healthy

user-service:

condition: service_started

acg-product-service:

condition: service_started

acg-rating-service:

condition: service_started

networks:

\- acg-demo-network

restart: always

frontend:

image: nginx:alpine

container_name: acg-frontend

restart: always

ports:

\- "5500:80"

volumes:

\- ./acg-frontend/dist:/usr/share/nginx/html:ro

\- ./nginx.conf:/etc/nginx/conf.d/default.conf:ro

networks:

\- acg-demo-network

networks:

acg-demo-network:

driver: bridge

volumes:

mysql_data:

数据库初始化

mysql-init/init.sql：

\-- =====================================

\-- 创建用户服务数据库

\-- =====================================

CREATE DATABASE IF NOT EXISTS acg_user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

\-- =====================================

\-- 创建产品服务数据库

\-- =====================================

CREATE DATABASE IF NOT EXISTS acg_product_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

\-- =====================================

\-- 创建评分服务数据库

\-- =====================================

CREATE DATABASE IF NOT EXISTS acg_rating_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

#### 测试验证

构建与启动

docker-compose build

docker-compose up -d

容器状态检查

docker-compose ps

预期输出：

NAME STATUS PORTS

acg-demo-mysql Up (healthy) 0.0.0.0:3306->3306/tcp

nacos Up (healthy) 0.0.0.0:8848->8848/tcp

user-service Up 0.0.0.0:8081->8081/tcp

acg-product-service Up 0.0.0.0:8082->8082/tcp

acg-rating-service Up 0.0.0.0:8083->8083/tcp

gateway-service Up 0.0.0.0:8090->8090/tcp

acg-frontend Up 0.0.0.0:5500->80/tcp

服务健康检查

\# Nacos 服务注册列表

curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=user-service

\# Gateway 路由检查

curl http://localhost:8090/actuator/gateway/routes

\# 微服务端到端调用测试

curl http://localhost:8090/api/v1/users

日志查看

docker-compose logs -f

docker-compose logs -f user-service

扩展服务实例

docker-compose up -d --scale user-service=3

\# 测试负载均衡

for i in {1..10}; do

curl http://localhost:8090/api/v1/users/1

echo ""

done

资源与性能监控

docker stats --no-stream

示例：

CONTAINER CPU % MEM USAGE / LIMIT NET I/O

user-service 2.5% 512MB / 2GB 1.2kB / 890B

acg-product-service 3.0% 480MB / 2GB 980B / 650B

acg-rating-service 1.8% 450MB / 2GB 2.1kB / 1.5kB

nacos 5.2% 1.2GB / 4GB 3.2kB / 2.1kB

acg-demo-mysql 8.5% 800MB / 4GB 4.5kB / 3.2kB

总结

1.  使用 Docker Compose 成功实现了 **ACG 微服务系统的一键部署**。
2.  所有微服务、数据库、配置中心、前端均通过容器化运行，端口映射和依赖关系清晰。
3.  可通过扩展服务实例和监控资源使用，实现高可用和负载均衡。
4.  系统验证包括 Nacos 服务注册、Gateway 路由、微服务端到端调用，均正常。

## 六、系统测试

### 6.1功能测试

功能测试主要验证系统各微服务模块在正常业务场景下是否能够按照设计要求正确运行。本系统采用微服务架构，包含用户服务、ACG 产品服务、资源管理服务以及评分与评论服务。  
测试重点包括：**用户注册与登录、产品管理、资源上传与下载、评分与评论功能**，以及各服务之间的协同工作情况。

### 6.2测试用例设计

#### 用户服务功能测试（User Service）

| **测试用例ID** | **功能模块** | **测试场景** | **测试步骤** | **预期结果** |
| --- | --- | --- | --- | --- |
| TC001 | 用户注册 | 新用户注册 | 调用 /user/register，传入未存在的用户名和密码 | 返回“注册成功”，用户数据写入数据库 |
| TC002 | 用户注册 | 用户名重复注册 | 使用已存在用户名再次调用 /user/register | 返回“用户名已存在” |
| TC003 | 用户登录 | 正确用户名和密码 | 调用 /user/login | 返回 Result，data 字段中包含 JWT Token |
| TC004 | 用户登录 | 错误用户名或密码 | 调用 /user/login | 返回“用户名或密码错误” |
| TC005 | 用户查询 | 根据 ID 查询用户 | 调用 /user/{id} | 返回对应用户信息 |
| TC006 | 用户删除 | 删除存在的用户 | 调用 /user/{id}（DELETE） | 返回“删除成功”，用户被软删除 |

#### ACG 产品管理功能测试（AcgProduct Service）

| **测试用例ID** | **功能模块** | **测试场景** | **测试步骤** | **预期结果** |
| --- | --- | --- | --- | --- |
| TC007 | 产品新增 | 新增 ACG 产品（无封面） | 调用 /acg-product，提交产品基本信息 | 产品成功创建，返回成功结果 |
| TC008 | 产品新增 | 新增 ACG 产品（含封面） | 调用 /acg-product，上传封面图片 | 产品创建成功，封面路径正确保存 |
| TC009 | 产品查询 | 查询全部产品 | 调用 /acg-product | 返回所有未删除的产品列表 |
| TC010 | 产品查询 | 按类型查询产品 | 调用 /acg-product/type/{type} | 返回指定类型产品列表 |
| TC011 | 产品分页 | 分页查询产品 | 调用 /acg-product/page | 返回分页数据，数量正确 |
| TC012 | 产品删除 | 删除指定产品 | 调用 /acg-product/{id}（DELETE） | 返回“删除成功”，产品被软删除 |
| TC013 | 产品查询 | 根据 ID 查询产品 | 调用 /acg-product/{id} | 返回对应产品信息 |
| TC014 | 产品评分更新 | 更新产品评分 | 调用 /acg-product/{id}/score | 产品评分字段更新成功 |

#### ACG 资源管理功能测试（AcgResource Service）

| **测试用例ID** | **功能模块** | **测试场景** | **测试步骤** | **预期结果** |
| --- | --- | --- | --- | --- |
| TC015 | 资源上传 | 上传压缩资源 | 调用 /acg-resource/upload，上传 zip/rar 文件 | 资源信息保存成功，返回资源对象 |
| TC016 | 资源查询 | 查询产品资源列表 | 调用 /acg-resource/list/{productId} | 返回指定产品下的资源列表 |
| TC017 | 资源下载 | 下载存在的资源 | 调用 /acg-resource/download/{id} | 成功下载对应资源文件 |
| TC018 | 资源下载 | 下载不存在的资源 | 使用错误的资源 ID | 返回 404，提示资源不存在 |

评分与评论功能测试（AcgRating & AcgComment Service）

| **测试用例ID** | **功能模块** | **测试场景** | **测试步骤** | **预期结果** |
| --- | --- | --- | --- | --- |
| TC020 | 产品评分 | 用户对产品评分 | 调用 /acg-rating/rate | 返回“评分成功”，评分记录保存 |
| TC021 | 产品评分 | 多用户评分 | 不同用户多次评分同一产品 | 产品平均评分正确更新 |
| TC022 | 评论添加 | 用户发表评论 | 调用 /acg-comment/add | 评论成功保存 |
| TC023 | 评论查询 | 查询产品评论 | 调用 /acg-comment/list/{productId} |     |

### 6.3测试执行

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC001 | 用户注册 | 新用户注册 | 调用 /user/register，传入未存在的用户名和密码 | 返回“注册成功”，用户数据写入数据库 |

TC002

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC003 | 用户登录 | 正确用户名和密码 | 调用 /user/login | 返回 Result，data 字段中包含 JWT Token |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC004 | 用户登录 | 错误用户名或密码 | 调用 /user/login | 返回“用户名或密码错误” |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC005 | 用户查询 | 根据 ID 查询用户 | 调用 /user/{id} | 返回对应用户信息 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC006 | 用户删除 | 删除存在的用户 | 调用 /user/{id}（DELETE） | 返回“删除成功”，用户被软删除 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC007 | 产品新增 | 新增 ACG 产品（无封面） | 调用 /acg-product，提交产品基本信息 | 产品成功创建，返回成功结果 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC008 | 产品新增 | 新增 ACG 产品（含封面） | 调用 /acg-product，上传封面图片 | 产品创建成功，封面路径正确保存 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC009 | 产品查询 | 查询全部产品 | 调用 /acg-product | 返回所有未删除的产品列表 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC010 | 产品查询 | 按类型查询产品 | 调用 /acg-product/type/{type} | 返回指定类型产品列表 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC011 | 产品分页 | 分页查询产品 | 调用 /acg-product/page | 返回分页数据，数量正确 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC012 | 产品删除 | 删除指定产品 | 调用 /acg-product/{id}（DELETE） | 返回“删除成功”，产品被软删除 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC013 | 产品查询 | 根据 ID 查询产品 | 调用 /acg-product/{id} | 返回对应产品信息 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC014 | 产品评分更新 | 更新产品评分 | 调用 /acg-product/{id}/score | 产品评分字段更新成功 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC015 | 资源上传 | 上传压缩资源 | 调用 /acg-resource/upload，上传 zip/rar 文件 | 资源信息保存成功，返回资源对象 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC016 | 资源查询 | 查询产品资源列表 | 调用 /acg-resource/list/{productId} | 返回指定产品下的资源列表 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC017 | 资源下载 | 下载存在的资源 | 调用 /acg-resource/download/{id} | 成功下载对应资源文件 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC018 | 资源下载 | 下载不存在的资源 | 使用错误的资源 ID | 返回 404，提示资源不存在 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC020 | 产品评分 | 用户对产品评分 | 调用 /acg-rating/rate | 返回“评分成功”，评分记录保存 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC021 | 产品评分 | 多用户评分 | 不同用户多次评分同一产品 | 产品平均评分正确更新 |

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| TC022 | 评论添加 | 用户发表评论 | 调用 /acg-comment/add | 评论成功保存 |

|     |     |     |     |
| --- | --- | --- | --- |
| TC023 | 评论查询 | 查询产品评论 | 调用 /acg-comment/list/{productId} |

## 性能测试

### 测试环境

- **硬件配置**：\[CPU/内存/磁盘\]
- **软件版本**：\[操作系统/Docker版本\]
- **测试工具**：Apache JMeter / wrk

### 测试场景

#### 场景1：单服务压力测试

**测试参数**：

- 并发用户数：100
- 测试时长：60秒
- 请求路径：GET /api/v1/users

**测试命令**：

wrk -t 4 -c 100 -d 60s http://localhost:8082/acg-product

**测试结果**：

Running 1m test @ http://localhost:8082/acg-product

4 threads and 100 connections

Thread Stats Avg Stdev Max +/- Stdev

Latency 47.12ms 51.96ms 544.07ms 85.44%

Req/Sec 722.02 290.24 2.54k 71.45%

172077 requests in 1.00m, 366.00MB read

Requests/sec: 2864.10

Transfer/sec: 6.09MB

## 集成测试

### 端到端测试流程

# 项目总结

## 技术亮点

### 1\. 全配置上传nacos

利用脚本批量将nacos配置上传。

### 2\. 项目资源上传和管理

在docker中创建卷来存储资源。利用网关将资源转化为路由的形式来获取。

### 3\. 全项目双环境部署

配置利用spring.config.import: nacos:${spring.application.name}.yaml 实现双环境docker的切换

### 4\. 全model软删除

所有的实体全部用软删除的方式，用isdelete加强微服务之间的解耦合

### 5\. 全后端前端匹配

具有比较美观的前端页面来显示后端的用法

## 遇到的问题与解决方案

### 问题1：资源在有网关和docker部署的时候的存储于读取

**问题现象**：在一开始我是把资源（图片、压缩包）上传到项目根目录中，然后再代码里用路径拼接来实现存储。但是在部署的时候发现docker环境没办法去读根目录，而是用的工作目录。

**原因分析**：docker的工作目录不等于在windows中运行的项目根目录，不能简单的用项目根目录读取之后去拼接完成

**解决方案**：所以最后明白原理之后在docker-compose中创建一个资源卷并绑定好需要存储的文件夹。完成资源的顺利存储

### 问题2：将yaml文件放到nacos中要手动创建的问题

**问题现象**：我比较快速的完成了yaml放到nacos的配置中心部署，但是问题是一开始一直都是用手动创建的方式，没有想到如何用脚本的方式来完成这一步骤

**原因分析**：没有学习过怎么用脚本来在nacos创建yaml

**解决方案**：在网上查找资料，最终明白了如何用url来将文件夹里的yaml文件逐个批量上传到nacos服务中心。

### 问题3：前端制作jwt导致api调用困难问题

**问题现象**：我在学习完成登录获取token的后端之后，在制作前端，发现前端不能再简单的用直接调用api的形式了

**原因分析**：前端需要去在每次调用接口的时候拼接上token

**解决方案**： 写了一个响应https处理js完成这一任务

import axios from "axios";

// ====== 调试开关（开发开，生产关） ======

const DEBUG_HTTP = true;

// 创建 Axios 实例

const http = axios.create({

baseURL: "http://localhost:8090", // ✅ 网关地址

timeout: 10000,

withCredentials: true, // 如果后端需要 cookies 或 allowCredentials

});

// ================= 请求拦截器 =================

http.interceptors.request.use(

config => {

const token = localStorage.getItem("token");

if (token) {

config.headers.Authorization = \`Bearer ${token.trim()}\`;

}

if (DEBUG_HTTP) {

console.groupCollapsed(

\`%c➡️ ${config.method?.toUpperCase()} ${config.url}\`,

"color:#67C23A;font-weight:bold"

);

console.log("Request config:", config);

console.groupEnd();

}

return config;

},

error => Promise.reject(error)

);

// ================= 响应拦截器 =================

http.interceptors.response.use(

response => {

if (DEBUG_HTTP) {

console.groupCollapsed(

\`%c⬅️ ${response.config.method?.toUpperCase()} ${response.config.url}\`,

"color:#409EFF;font-weight:bold"

);

console.log("Status:", response.status);

console.log("Result:", response.data);

console.groupEnd();

}

return response.data;

},

error => {

if (DEBUG_HTTP) {

console.groupCollapsed(

\`%c❌ HTTP ERROR ${error.config?.url}\`,

"color:red;font-weight:bold"

);

console.log("Error:", error);

console.groupEnd();

}

if (error.response && error.response.status === 401) {

alert("登录已过期或未登录，请重新登录");

localStorage.removeItem("token");

setTimeout(() => {

window.location.href = "/login";

}, 500);

}

return Promise.reject(error);

}

);

export default http;

## 个人收获

1.  **微服务架构理解**：
    - 学会了如何用网关与三个微服务之间相互配合以及路由配置
    - 学会了在网关配置全局的跨域访问
    - 更好的理解了扩服务之间如何进行api调用
2.  **技术能力提升**：
    - 在微服务的架构下进行jwt验证与前端的访问
    - 在docker中进行前端部署和跨域访问
    - 学会了前端进行统一的url拼接和response类的接收处理
3.  **工程实践经验**：
    - 明白了如何在配置中心nacos统一进行配置文件部署，学会了如何在脚本进行批量nacos配置文件上传
    - 学会了如何让nacos先行在docker部署，加上健康检查保证后续服务能读取配置

## 未来改进方向

1.  **功能扩展**：
    - 可以再增加一些功能，丰富功能
    - 可以再加入如消息队列，使得文件上传更方便
2.  **性能优化**：
    - **数据库优化**：对常用查询增加索引，优化分页和模糊搜索性能
    - **缓存机制**：使用 Redis 对热点数据（如热门产品、用户评分）进行缓存，降低数据库压力
3.  **运维完善**：
    - **监控与报警**：引入 Prometheus + Grafana 对服务健康、CPU、内存、数据库连接等指标监控，异常自动报警。
    - **日志集中化**：使用 ELK/EFK（Elasticsearch + Logstash/Fluentd + Kibana）集中收集和分析日志，便于问题排查

# 附录

## 完整API文档数据库ER图参考资料

1.  Spring Cloud官方文档：https://spring.io/projects/spring-cloud
2.  Nacos官方文档：https://nacos.io/
3.  RabbitMQ官方文档：https://www.rabbitmq.com/
4.  Docker官方文档：https://docs.docker.com/
5.  课程PPT和演示代码

## 项目仓库

- **Git仓库地址**：[yyd-git/acgweb-service](https://github.com/yyd-git/acgweb-service)


**声明**：本报告中的所有代码和配置均为本人独立完成，参考资料已在文末列出。