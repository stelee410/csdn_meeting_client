# 用户账号安全功能实现总结

## 实现日期
2026年4月5日

## 功能概述

实现了4个用户账号安全相关功能：

| 功能 | 状态 | 说明 |
|-----|-----|-----|
| **修改密码** | ✅ 新增 | 用户主动修改密码，需原密码验证 |
| **邮箱换绑** | ✅ 新增 | 专门更换绑定邮箱流程，需身份验证+新邮箱验证 |
| **注销账号** | ✅ 新增 | 用户主动注销，需验证码验证，账号永久失效 |
| **退出账号** | ✅ 已存在 | 已完整实现，Token加入黑名单 |

---

## 接口清单

### 新增接口

| 功能 | 接口路径 | 方法 | 请求参数 | 说明 |
|-----|---------|-----|---------|-----|
| 修改密码 | `/api/users/password/change` | POST | ChangePasswordCommand | 需原密码验证 |
| 更换邮箱 | `/api/users/email/change` | POST | ChangeEmailCommand | 需身份验证+新邮箱验证 |
| 注销账号 | `/api/users/cancel` | POST | CancelAccountCommand | 需验证码验证，不可逆 |

### 已存在接口（无需修改）

| 功能 | 接口路径 | 方法 | 说明 |
|-----|---------|-----|-----|
| 退出登录 | `/api/auth/logout` | POST | Token加入黑名单 |
| 发送邮箱验证码 | `/api/auth/verification-code/email` | POST | 复用现有接口 |
| 发送短信验证码 | `/api/auth/verification-code/sms` | POST | 复用现有接口 |

---

## 文件变更清单

### 新增文件

| 文件 | 说明 |
|-----|-----|
| `csdn-meeting-application/src/main/java/com/csdn/meeting/application/dto/ChangePasswordCommand.java` | 修改密码请求DTO |
| `csdn-meeting-application/src/main/java/com/csdn/meeting/application/dto/ChangeEmailCommand.java` | 更换邮箱请求DTO |
| `csdn-meeting-application/src/main/java/com/csdn/meeting/application/dto/CancelAccountCommand.java` | 注销账号请求DTO |

### 修改文件

| 文件 | 变更内容 |
|-----|---------|
| `csdn-meeting-domain/src/main/java/com/csdn/meeting/domain/valueobject/UserStatus.java` | 新增 `CANCELLED(2, "已注销", "cancelled")` 枚举值 |
| `csdn-meeting-domain/src/main/java/com/csdn/meeting/domain/valueobject/VerificationCodeScene.java` | 新增 `CHANGE_EMAIL_OLD`、`CHANGE_EMAIL_NEW`、`CANCEL_ACCOUNT` 场景 |
| `csdn-meeting-domain/src/main/java/com/csdn/meeting/domain/entity/User.java` | 新增 `cancel()` 和 `isCancelled()` 方法 |
| `csdn-meeting-domain/src/main/java/com/csdn/meeting/domain/service/UserDomainService.java` | 新增 `cancelUser()`、`findByEmail()` 方法，更新 `canLogin()` 检查注销状态 |
| `csdn-meeting-application/src/main/java/com/csdn/meeting/application/service/UserProfileAppService.java` | 新增 `changePassword()`、`changeEmail()`、`cancelAccount()` 方法 |
| `csdn-meeting-interfaces/src/main/java/com/csdn/meeting/interfaces/controller/UserProfileController.java` | 新增修改密码、更换邮箱、注销账号接口 |

---

## 安全设计

### 1. 修改密码
- **原密码验证**: 必须提供正确的原密码才能修改
- **新密码强度**: 至少8位，包含字母和数字
- **确认密码**: 新密码与确认密码必须一致
- **防重复**: 新密码不能与原密码相同

### 2. 邮箱换绑
- **身份验证**: 需提供原邮箱验证码或短信验证码（二选一）
- **新邮箱验证**: 必须验证新邮箱所有权
- **重复检查**: 新邮箱不能已被其他用户绑定

### 3. 注销账号
- **强身份验证**: 需提供邮箱验证码或短信验证码（二选一）
- **状态检查**: 已注销账号无法再次注销
- **登录拦截**: 注销后用户无法登录（Token验证时检查账号状态）

### 4. 退出账号
- **Token黑名单**: 退出时Token立即加入黑名单
- **全局生效**: 所有使用该Token的请求都会被拦截

---

## 数据库变更

**无需DDL变更**，使用现有 `status` 字段：

| 状态值 | 说明 |
|-------|-----|
| 0 | NORMAL - 正常 |
| 1 | FROZEN - 冻结 |
| 2 | CANCELLED - 已注销（新增）|

---

## 后续优化建议

1. **注销冷静期**: 建议增加7天冷静期，期间可撤销注销
2. **数据保留**: 注销后用户数据可根据合规要求保留一定时间
3. **敏感操作日志**: 建议记录所有密码修改、邮箱换绑、账号注销操作日志
4. **异常通知**: 敏感操作后发送通知邮件给用户

---

## 测试建议

1. **修改密码**:
   - 正确原密码修改成功
   - 错误原密码修改失败
   - 新密码与确认密码不一致失败
   - 新密码强度不足失败
   - 新密码与原密码相同失败

2. **邮箱换绑**:
   - 原邮箱验证身份换绑成功
   - 短信验证身份换绑成功
   - 未提供任何验证码失败
   - 错误验证码失败
   - 新邮箱已被绑定失败
   - 新邮箱验证码错误失败

3. **注销账号**:
   - 邮箱验证注销成功
   - 短信验证注销成功
   - 未提供任何验证码失败
   - 错误验证码失败
   - 已注销账号再次注销失败
   - 注销后登录失败

4. **退出账号**:
   - 退出后Token失效
   - 退出后使用原Token访问需登录接口失败
