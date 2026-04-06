# 登出后旧 Token 立即失效 — 实现计划

## 目标

- `POST /api/auth/logout` 在收到**当前会话的 Bearer Token** 时，将该 Token 标记为已撤销。
- [LoginInterceptor](csdn-meeting-interfaces/src/main/java/com/csdn/meeting/interfaces/config/LoginInterceptor.java) 在校验 JWT 签名与过期时间**之后**，增加一步：**若 Token 在撤销集合中则拒绝**（返回 401，与现有未登录语义一致）。
- WebSocket 握手 [JwtHandshakeInterceptor](csdn-meeting-interfaces/src/main/java/com/csdn/meeting/interfaces/interceptor/JwtHandshakeInterceptor.java) 在 `validateToken` 通过后同样检查撤销，避免 HTTP 已登出仍连上 WS。

## 方案对比（二选一或组合）

### A. Token 黑名单（推荐用于「只作废当前这一串 Token」）

| 项 | 说明 |
|----|------|
| 登出 | 从 `Authorization: Bearer <token>` 取 token，计算 **SHA-256（hex）** 作为 key，写入撤销存储；**TTL** 设为该 JWT **剩余有效期**（至 `exp`），避免永久堆积。 |
| 校验 | 拦截器中：`validateToken` 通过后，对同一 token 算 hash，查是否在黑名单。 |
| 优点 | 不改动 JWT 载荷结构；登出只影响当前 token。 |
| 多实例 | 单机可用进程内存储；**多实例/滚动发布**需共享存储（如 Redis），否则各节点黑名单不一致。 |

### B. 用户维度 Token 版本（「一次登出，该用户所有旧 JWT 失效」）

| 项 | 说明 |
|----|------|
| 签发 | JWT 增加 claim：`tv`（long，用户当前 token 版本），来源 DB/Redis。 |
| 登出 | 用户 `token_version` +1（或 Redis `user:{id}:tv`）。 |
| 校验 | `validateToken` 通过后比对 JWT 内 `tv` 与存储是否一致。 |
| 优点 | 实现后天然多实例一致（若版本存 DB/Redis）；无需按 token 存哈希。 |
| 缺点 | 需改登录签发与可能的数据库字段；登出会踢掉**所有设备**会话。 |

**已确认（产品选择）**：仅需 **作废当前这一串 Token** → 实施 **方案 A（Token 黑名单）**。方案 B 留作后续「全端下线」等需求时再扩展。

## 方案 A 的具体改动（文件级）

1. **基础设施层**新增 `TokenRevocationStore`（或命名 `TokenBlacklistService`）  
   - 接口：`void revokeToken(String jwtRaw)`、`boolean isRevoked(String jwtRaw)`。  
   - 实现：用 **Caffeine** `Cache<String, Boolean>`（需新增依赖）或 `ConcurrentHashMap` + 记录过期时间并惰性清理（零依赖，适合单机）。  
   - `revokeToken`：若无法解析 `exp`，可退回使用 [JwtTokenProvider](csdn-meeting-infrastructure/src/main/java/com/csdn/meeting/infrastructure/security/JwtTokenProvider.java) 的过期配置作为 TTL 上限。

2. **JwtTokenProvider**  
   - 增加从 token 解析 **`exp`（过期时间）** 的方法，供计算黑名单 TTL（剩余秒数）。  
   - 保持现有 `generateToken` / `validateToken` 不变，避免影响已有登录。

3. **UserAuthController.logout**  
   - 读取 `Authorization`，若为 `Bearer` 且非空，则 `validateToken` 通过后调用 `revokeToken`。  
   - 无 Token 或格式不对：可返回 200（与「前端删 token」一致）或 401；需在接口上定一条明确语义（建议：**无 token 仍 200**，仅在有有效 token 时写入撤销）。

4. **LoginInterceptor**  
   - 在设置 `currentUserId` 之前：`if (isRevoked(token))` 则写 401 响应并 `return false`。

5. **JwtHandshakeInterceptor**  
   - `validateToken` 通过后同样 `isRevoked(token)`，否则握手失败。

6. **依赖与模块**  
   - 若采用 Caffeine：在合适模块（多为 `csdn-meeting-infrastructure`）的 `pom.xml` 增加 `caffeine`；若坚持零新依赖，则用 `ConcurrentHashMap` + 按 `exp` 清理策略。

## 后续扩展（非本迭代必做）

- 生产多实例：将 `TokenRevocationStore` 实现替换为 **Redis**（`SETEX jwt:blacklist:<hash> 1 <ttl>`）。  
- 与方案 B 组合：账号安全中心「全部下线」时递增版本号 + 可选仍保留黑名单做细粒度控制。

## 测试要点

- 登录拿 token A → 调 logout（带 A）→ 再带 A 调需登录接口 → 401。  
- 登录拿 token A → 不调 logout → 带 A 调接口 → 仍 200。  
- WebSocket：logout 后同一 token 握手应失败。
