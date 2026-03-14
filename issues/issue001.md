前后端联调问题记录
1. 上传图片 (api/images/upload)

问题描述: 返回的图片 url 路径拼接异常，含有两个 http 和重复的端口号。

示例: "url": "http://http://47.94.193.166:5678/:5678/uploads/images/2026/0d1.png"

2. 创建/编辑模板 (/api/meeting-templates)

问题描述: 缺少“会议时长”、“会议规模”、“举办频率”字段。需确认这三个字段的数据是前端写死，还是后端会提供下拉选项接口？

3. 创建/编辑模板 (/api/meeting-templates)

问题描述: “目标人群”字段保存失败。接口文档中定义为 string 类型，但实际传数字类型才能存进去，请核对字段类型。

4. 创建/编辑模板 (/api/meeting-templates)

问题描述: 所有新添加的模板，保存后的默认状态都是“已上架”，逻辑疑似异常。

5. 模板下架 (/api/meeting-templates)

问题描述: 触发 Bug。点击将某一个模板下架后，再请求模板列表接口，直接返回了空数据。

6. 创建会议

问题描述: 缺少字典数据。创建会议时用到的“会议举办地域（省/市）”、“目标人群”、“开发者类型”，目前没找到对应的下拉选项接口。

7. 创建会议 (/api/meetings)

问题描述:

接口文档中要求 scheduleDays[].sessions[] 下必须包含 subVenues 数组（子会场信息），但在产品原型图上没有找到子会场的设计，需确认该字段是否可改为非必填项。

请求参数中发现图片 URL 存在双斜杠拼接问题（如 http://47.94.193.166:5678//uploads/images/...），请一并排查静态资源路径处理逻辑。

8. 获取指定会议的详情 (/api/meetings/{id})

问题描述:

coverImage 字段缺失。

startDate、endDate 数据返回格式有误（示例：startDate: "2026-03-24T00:00:00.000Z"，带有 T 和 Z 等时区后缀，需确认前端是否需要自行格式化，还是后端统一返回标准格式）。

9. 热门标签

问题描述: 缺少热门标签数据的获取接口。

10. 创建会议 (/api/meetings) —— 时间字段格式错误导致 400

问题描述:

前端发送请求体时，两处时间字段格式与后端期望不一致，导致 Jackson 反序列化失败，返回"请求体格式错误或参数类型不正确"。

（a）外层 startTime / endTime（对应 CreateMeetingCommand.LocalDateTime）：
前端发送带 UTC 时区后缀的 ISO-8601 字符串，如 "2026-03-14T16:00:00.000Z"，
而 LocalDateTime 的默认解析器不支持 "Z" 后缀，会抛出解析异常。

（b）scheduleDays[].sessions[].startTime / endTime（对应 SessionDTO.LocalTime）：
前端发送 Java 对象格式 {"hour":12,"minute":2,"second":0,"nano":0}，
而 LocalTime 的 Jackson 默认解析器期望字符串格式，如 "12:02:00"，无法识别对象格式。

后端修复方案：
已在 csdn-meeting-start/src/main/java/com/csdn/meeting/config/JacksonConfig.java 中增加自定义反序列化器：
- FlexibleLocalDateTimeDeserializer：兼容带/不带时区后缀的 ISO-8601 字符串
- FlexibleLocalTimeDeserializer：同时兼容字符串格式（"12:02:00"）和对象格式（{"hour":12,...}）

前端建议格式（可不改前端，后端已兼容）：
- startTime / endTime → "2026-03-14T16:00:00.000Z" ✓（后端已兼容）
- sessions[].startTime / endTime → "12:02:00" 或 {"hour":12,"minute":2,"second":0,"nano":0} ✓（后端已兼容）