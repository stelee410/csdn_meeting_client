# CSDN 通知开放平台接口文档

## 1. 接口概述
本接口用于第三方应用向 CSDN 用户发送通知消息。为了更好的隔离性，不同通道的消息请调用对应的接口地址。

*   **Base URL**: `https://msg.csdn.net`
*   **Content-Type**: `application/json`

## 2. 鉴权机制
所有请求必须包含以下 Header：

| Header 字段 | 说明 | 示例 |
| :--- | :--- | :--- |
| `X-App-Key` | 应用唯一标识 (分配) | `testApp` |
| `X-Timestamp` | 当前时间戳 (毫秒) | `1678888888000` |
| `X-Nonce` | 随机字符串 | `abc123xyz` |
| `X-Signature` | 签名摘要 (HMAC-SHA256) | `d8e8fca2...` |

### 签名算法
1.  **准备数据**：将 `AppKey`、`Timestamp`、`Nonce` 和请求体原始字符串（Raw Body）按顺序拼接。
    ```
    Data = AppKey + Timestamp + Nonce + Body
    ```
    **重要提示**：
    *   **Body 必须是 Raw String**：参与签名的 Body 字符串必须与最终发送给 HTTP 服务器的请求体内容**严格一致**（包括所有空格、换行符、字段顺序）。
    *   **禁止二次序列化**：客户端在生成签名后，**不要**再对 Body 进行 JSON 解析或格式化，直接将用于签名的那个字符串作为 HTTP Request Body 发送。
    *   *建议*：为了便于调试和避免不同 JSON 库的序列化差异，建议客户端在构建 JSON 时按 Key 的 ASCII 码升序排列（虽非强制，但推荐）。

2.  **计算签名**：使用分配的 `AppSecret` 对拼接后的字符串进行 HMAC-SHA256 计算，并将结果转换为 Base64 字符串。
    ```
    Signature = Base64(HMAC-SHA256(AppSecret, Data))
    ```

**Java 示例代码**:
```java
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.TreeMap;
import com.alibaba.fastjson.JSONObject;

public class SignDemo {
    public static void main(String[] args) throws Exception {
        String appKey = "testApp";
        String appSecret = "your_secret_here";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String nonce = "random_string";

        // 1. 构建有序的业务参数 (推荐做法，非强制)
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("templateCode", "activity_notify_v1");
        params.put("toUsers", new String[]{"user_123", "user_456"});
        
        // 生成 JSON 字符串 (作为 Body)
        String jsonBody = JSONObject.toJSONString(params); 

        // 2. 拼接数据
        String data = appKey + timestamp + nonce + jsonBody;

        // 3. 计算签名
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        String signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));

        System.out.println("Signature: " + signature);
        System.out.println("Body to send: " + jsonBody);
    }
}
```

## 3. 发送消息接口

AppKey:Conference_New_Notice
PUSH模板:New_Notice_PUSH
IM模板:New_Notice_IM


### 3.1 IM 站内信发送
**URL**: `/im/open/v1/send` (完整路径: `https://msg.csdn.net/im/open/v1/send`)
**Method**: `POST`

### 3.2 APP 推送发送
**URL**: `/push/open/v1/send` (完整路径: `https://msg.csdn.net/push/open/v1/send`)
**Method**: `POST`

**请求参数 (JSON Body)**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `templateCode` | String | 是 | 模板编码 (在开放平台申请的模板名称) |
| `toUsers` | List\<String\> | 是 | 接收用户的 CSDN ID 列表 (单次最多 1000 人) |
| `params` | Map\<String, String\> | 否 | 模板变量，用于替换模板中的 `{key}` 占位符 |

**请求示例**:
```json
{
  "templateCode": "activity_notify_v1",
  "toUsers": ["user_123", "user_456"],
  "params": {
    "activityName": "2023开发者大会",
    "time": "2023-10-24"
  }
}
```

**响应参数**:

| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| `status` | Boolean | 请求状态 (true: 成功, false: 失败) |
| `code` | String | 状态码 (200: 成功, 4xx: 客户端错误, 5xx: 服务端错误) |
| `message` | String | 提示信息 |

**响应示例 (成功)**:
```json
{
  "status": true,
  "code": "200",
  "message": "Message sent successfully",
  "data": null
}
```

**响应示例 (失败)**:
```json
{
  "status": false,
  "code": "429",
  "message": "Rate limit exceeded for user: user_123"
}
```

## 4. 错误码说明

| 状态码 | 说明 |
| :--- | :--- |
| `200` | 发送成功 |
| `400` | 请求体格式错误 |
| `401` | 认证失败 (Missing headers / Invalid AppKey / Invalid Signature) |
| `404` | 模板不存在或无权访问 |
| `429` | 触发频控限制 (单用户单日发送超限) |
| `500` | 服务器内部错误 |

## 5. 注意事项
1.  **模板配置**：调用前请确保已在后台配置好 `templateCode` 对应的模板，并正确设置了 `channel` (IM 或 push)。
2.  **通道选择**：请根据模板的 `channel` 类型选择正确的接口地址。如果模板配置为 push 但调用了 im 接口（或反之），可能会导致发送失败或不符合预期。
3.  **推送 URL**：如果是 PUSH 类型的模板，`url` 字段必须是合法的 HTTP/HTTPS 链接。
4.  **频控**：默认受单用户单日，超限将返回 429。
