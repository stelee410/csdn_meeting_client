# 短信和邮件服务接入指南

## 概述

本文档描述如何将CSDN会议系统的短信和邮件服务从Mock模式切换到火山云SMS和阿里云DM（DirectMail）真实服务。

---

## 架构说明

```
┌─────────────────────────────────────────────────────────────────┐
│                      Application层                               │
│                  VerificationCodeAppService                       │
└─────────────────────────┬───────────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────────┐
│                    Infrastructure层                              │
│  ┌─────────────────┐               ┌─────────────────────┐      │
│  │   SmsService    │               │    EmailService     │      │
│  │     接口        │               │       接口          │      │
│  └────────┬────────┘               └──────────┬──────────┘      │
│           │                                   │                 │
│  ┌────────▼────────┐               ┌───────────▼──────────┐      │
│  │ VolcSmsService  │               │ AliyunEmailService   │      │
│  │    Impl         │               │      Impl            │      │
│  │  (@Primary)     │               │    (@Primary)        │      │
│  └────────┬────────┘               └───────────┬──────────┘      │
│           │ (配置无效时降级)                      │ (配置无效时降级) │
│  ┌────────▼────────┐               ┌───────────▼──────────┐      │
│  │  SmsServiceImpl │               │  EmailServiceImpl      │      │
│  │    (Mock)       │               │     (Mock)            │      │
│  └─────────────────┘               └─────────────────────┘      │
└─────────────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────────┐
│                      第三方服务                                   │
│              火山云SMS        阿里云DM                            │
└─────────────────────────────────────────────────────────────────┘
```

---

## 环境变量配置清单

在启动应用前，需要配置以下环境变量：

### 火山云短信服务配置

| 环境变量 | 必填 | 说明 | 获取方式 |
|---------|------|------|---------|
| `VOLC_SMS_ACCESS_KEY_ID` | 是 | 火山云访问密钥ID | [火山云控制台](https://console.volcengine.com/iam/keymanage) - 密钥管理 |
| `VOLC_SMS_ACCESS_KEY_SECRET` | 是 | 火山云访问密钥Secret | 同上 |
| `VOLC_SMS_SIGN_NAME` | 是 | 短信签名 | [短信服务控制台](https://console.volcengine.com/sms) - 签名管理 |
| `VOLC_SMS_TEMPLATE_REGISTER` | 是 | 注册验证码模板ID | [短信服务控制台](https://console.volcengine.com/sms) - 模板管理 |
| `VOLC_SMS_TEMPLATE_LOGIN` | 是 | 登录验证码模板ID | 同上 |
| `VOLC_SMS_TEMPLATE_RESET_PASSWORD` | 是 | 重置密码模板ID | 同上 |
| `VOLC_SMS_TEMPLATE_GENERAL` | 否 | 通用验证码模板ID | 同上 |

### 阿里云邮件推送配置

| 环境变量 | 必填 | 说明 | 获取方式 |
|---------|------|------|---------|
| `ALI_DM_ACCESS_KEY_ID` | 是 | 阿里云访问密钥ID | [阿里云控制台](https://ram.console.aliyun.com/manage/ak) - AccessKey管理 |
| `ALI_DM_ACCESS_KEY_SECRET` | 是 | 阿里云访问密钥Secret | 同上 |
| `ALI_DM_FROM_ADDRESS` | 是 | 发件人邮箱地址 | [邮件推送控制台](https://dm.console.aliyun.com) - 发信地址 |
| `ALI_DM_FROM_ALIAS` | 否 | 发件人昵称（默认：CSDN会议系统） | - |
| `ALI_DM_REGION_ID` | 否 | 服务区域（默认：cn-hangzhou） | - |
| `ALI_DM_TAG_NAME` | 否 | 邮件标签（默认：meeting_verify） | [邮件推送控制台](https://dm.console.aliyun.com) - 邮件标签 |

---

## 配置示例

### Windows (PowerShell)

```powershell
# 火山云短信配置
$env:VOLC_SMS_ACCESS_KEY_ID = "your-volc-access-key-id"
$env:VOLC_SMS_ACCESS_KEY_SECRET = "your-volc-access-key-secret"
$env:VOLC_SMS_SIGN_NAME = "CSDN会议"
$env:VOLC_SMS_TEMPLATE_REGISTER = "SMS_xxx"
$env:VOLC_SMS_TEMPLATE_LOGIN = "SMS_xxx"
$env:VOLC_SMS_TEMPLATE_RESET_PASSWORD = "SMS_xxx"
$env:VOLC_SMS_TEMPLATE_GENERAL = "SMS_xxx"

# 阿里云邮件配置
$env:ALI_DM_ACCESS_KEY_ID = "your-ali-access-key-id"
$env:ALI_DM_ACCESS_KEY_SECRET = "your-ali-access-key-secret"
$env:ALI_DM_FROM_ADDRESS = "noreply@csdn.net"

# 启动应用
java -jar csdn-meeting-start-1.0.0-SNAPSHOT.jar
```

### Linux/macOS (Bash)

```bash
# 火山云短信配置
export VOLC_SMS_ACCESS_KEY_ID="your-volc-access-key-id"
export VOLC_SMS_ACCESS_KEY_SECRET="your-volc-access-key-secret"
export VOLC_SMS_SIGN_NAME="CSDN会议"
export VOLC_SMS_TEMPLATE_REGISTER="SMS_xxx"
export VOLC_SMS_TEMPLATE_LOGIN="SMS_xxx"
export VOLC_SMS_TEMPLATE_RESET_PASSWORD="SMS_xxx"
export VOLC_SMS_TEMPLATE_GENERAL="SMS_xxx"

# 阿里云邮件配置
export ALI_DM_ACCESS_KEY_ID="your-ali-access-key-id"
export ALI_DM_ACCESS_KEY_SECRET="your-ali-access-key-secret"
export ALI_DM_FROM_ADDRESS="noreply@csdn.net"

# 启动应用
java -jar csdn-meeting-start-1.0.0-SNAPSHOT.jar
```

### Docker

```dockerfile
# Dockerfile 或 docker-compose.yml 环境变量配置
ENV VOLC_SMS_ACCESS_KEY_ID=your-volc-access-key-id
ENV VOLC_SMS_ACCESS_KEY_SECRET=your-volc-access-key-secret
ENV VOLC_SMS_SIGN_NAME=CSDN会议
ENV VOLC_SMS_TEMPLATE_REGISTER=SMS_xxx
ENV VOLC_SMS_TEMPLATE_LOGIN=SMS_xxx
ENV VOLC_SMS_TEMPLATE_RESET_PASSWORD=SMS_xxx

ENV ALI_DM_ACCESS_KEY_ID=your-ali-access-key-id
ENV ALI_DM_ACCESS_KEY_SECRET=your-ali-access-key-secret
ENV ALI_DM_FROM_ADDRESS=noreply@csdn.net
```

---

## 接入步骤

### 步骤1：开通火山云短信服务

1. 登录 [火山云控制台](https://console.volcengine.com)
2. 进入 "短信服务 SMS" 产品页
3. 完成企业实名认证（个人认证无法使用短信服务）
4. 申请短信签名：
   - 类型：公司全称/公司简称/产品名称
   - 建议名称："CSDN会议" 或您的公司名称
   - 上传营业执照等证明材料
   - 等待审核（通常1-3个工作日）

5. 申请短信模板：
   - 模板类型：验证码
   - 模板内容示例：`您的验证码是${code}，5分钟内有效。如非本人操作，请忽略。`
   - 为每个场景申请模板：注册、登录、重置密码

6. 获取AccessKey：
   - 进入 "访问控制 IAM" → "密钥管理"
   - 创建新的AccessKey
   - 保存AccessKey ID和AccessKey Secret（Secret只显示一次）

### 步骤2：开通阿里云邮件推送服务

1. 登录 [阿里云控制台](https://console.aliyun.com)
2. 搜索并进入 "邮件推送 DirectMail" 产品页
3. 申请发信地址：
   - 发信域名：使用您的域名（如 csdn.net）
   - 发信地址：如 `noreply@csdn.net`
   - 完成域名验证（添加DNS记录）
   - 等待审核（通常1-2个工作日）

4. 创建邮件标签（可选）：
   - 标签名称：`meeting_verify`
   - 用于分类统计不同场景的邮件发送数据

5. 获取AccessKey：
   - 进入 "访问控制 RAM" → "AccessKey管理"
   - 创建新的AccessKey或使用现有AccessKey
   - 确保AccessKey有 `AliyunDirectMailFullAccess` 权限

### 步骤3：配置环境变量并部署

1. 在生产服务器上配置上述环境变量
2. 确保环境变量在应用启动时生效
3. 启动应用，观察日志确认服务初始化成功：
   ```
   火山云短信服务初始化成功，端点: sms.volcengineapi.com
   阿里云邮件服务初始化成功，区域: cn-hangzhou，发件人: noreply@csdn.net
   ```

### 步骤4：测试验证

1. **Mock模式测试**：
   - 不配置环境变量
   - 调用发送验证码接口
   - 查看日志应为：`[SMS Mock] 向手机号 138****8888 发送验证码: 123456, 场景: register`

2. **真实服务测试**：
   - 配置完整的环境变量
   - 调用发送验证码接口
   - 查看日志应为：`短信发送成功，手机号: 138****8888，场景: register，模板: SMS_xxx`
   - 检查手机/邮箱是否收到验证码

---

## 降级机制说明

当以下任一条件满足时，服务自动降级为Mock模式：

### 短信服务降级条件
- `VOLC_SMS_ACCESS_KEY_ID` 未配置或为空
- `VOLC_SMS_ACCESS_KEY_SECRET` 未配置或为空
- 对应场景的模板ID未配置（如注册时 `VOLC_SMS_TEMPLATE_REGISTER` 为空）

### 邮件服务降级条件
- `ALI_DM_ACCESS_KEY_ID` 未配置或为空
- `ALI_DM_ACCESS_KEY_SECRET` 未配置或为空
- `ALI_DM_FROM_ADDRESS` 未配置或为空

### 降级行为
- 打印 `WARN` 级别日志提示使用模拟模式
- 打印 `INFO` 级别日志记录发送内容（手机号/邮箱已脱敏）
- 返回 `true` 模拟发送成功
- 不影响业务流程，可用于开发和测试环境

---

## 重试机制

两个服务都实现了指数退避重试机制：

- **最大重试次数**：3次（可在配置中修改）
- **重试间隔**：1秒 × 尝试次数（第1次1秒，第2次2秒，第3次3秒）
- **触发重试的场景**：网络超时、HTTP错误、API返回错误

---

## 安全注意事项

1. **密钥安全**：
   - AccessKey Secret 不要提交到代码仓库
   - 生产环境使用环境变量或密钥管理服务（如KMS）
   - 定期轮换AccessKey

2. **短信防刷**：
   - 火山云默认有频率限制
   - 应用层已有限流措施（VerificationCodeAppService）
   - 建议配合图形验证码使用

3. **邮件反垃圾**：
   - 使用已备案域名
   - 保持发信地址稳定
   - 监控退信率和投诉率

---

## 故障排查

### 短信发送失败

| 现象 | 可能原因 | 解决方法 |
|-----|---------|---------|
| 日志显示"短信服务初始化成功"但发送失败 | 模板ID错误 | 检查 `VOLC_SMS_TEMPLATE_*` 配置是否正确 |
| 日志显示"短信服务初始化成功"但发送失败 | 签名审核未通过 | 登录火山云控制台确认签名状态为"已通过" |
| 日志显示"短信发送异常" | 网络问题 | 检查服务器是否能访问 `sms.volcengineapi.com` |
| 日志显示"使用模拟模式" | 配置未生效 | 检查环境变量是否正确设置，重启应用 |

### 邮件发送失败

| 现象 | 可能原因 | 解决方法 |
|-----|---------|---------|
| 日志显示"邮件服务初始化成功"但发送失败 | 发信地址未验证 | 登录阿里云控制台确认发信地址状态为"已验证" |
| 邮件被收件箱归类为垃圾邮件 | 发信域名信誉度低 | 使用备案域名，保持稳定发信频率 |
| 大量发送失败 | 超出日配额 | 登录阿里云控制台查看配额，申请提升额度 |
| 日志显示"使用模拟模式" | 配置未生效 | 检查环境变量是否正确设置，重启应用 |

---

## 监控和告警

建议配置的监控项：

1. **短信发送成功率** < 95% → 告警
2. **邮件发送成功率** < 95% → 告警
3. **降级模式触发次数** > 0 → 告警（表示配置缺失）

日志关键字：
- `火山云短信服务初始化成功` / `阿里云邮件服务初始化成功`
- `短信发送成功` / `邮件发送成功`
- `短信发送失败` / `邮件发送失败`
- `使用模拟模式`

---

## 回滚方案

如需临时回退到Mock模式：

1. **方案一：删除环境变量**
   - 删除或清空对应的环境变量
   - 重启应用
   - 服务自动降级到Mock模式

2. **方案二：禁用真实服务**（需要代码变更）
   - 在 `application.yml` 中删除或注释掉火山云/阿里云配置
   - 重新编译部署

---

## 相关代码文件

- 配置文件：`csdn-meeting-start/src/main/resources/application.yml`
- 短信配置类：`csdn-meeting-infrastructure/src/main/java/com/csdn/meeting/infrastructure/config/VolcSmsProperties.java`
- 邮件配置类：`csdn-meeting-infrastructure/src/main/java/com/csdn/meeting/infrastructure/config/AliyunDmProperties.java`
- 短信实现：`csdn-meeting-infrastructure/src/main/java/com/csdn/meeting/infrastructure/external/VolcSmsServiceImpl.java`
- 邮件实现：`csdn-meeting-infrastructure/src/main/java/com/csdn/meeting/infrastructure/external/AliyunEmailServiceImpl.java`
- Mock实现：`csdn-meeting-infrastructure/src/main/java/com/csdn/meeting/infrastructure/external/SmsServiceImpl.java`
- Mock实现：`csdn-meeting-infrastructure/src/main/java/com/csdn/meeting/infrastructure/external/EmailServiceImpl.java`

---

## 技术支持

- 火山云短信服务文档：https://www.volcengine.com/docs/6360
- 阿里云邮件推送文档：https://help.aliyun.com/product/29412.html
- CSDN会议系统内部支持：联系运维团队
