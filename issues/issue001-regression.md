# issue001 回归测试用例

基于 `issues/issue001.md` 的联调问题记录，本文件描述对应的代码修复及回归验证场景。

## 1. 上传图片 URL 路径拼接

**修复**：`LocalImageStorageClient` 增加 `normalizeAccessUrlPrefix` 和 `appendPathWithoutDoubleSlash`，避免重复 http、端口号及双斜杠。

**回归**：
- 单元测试：`LocalImageStorageClientTest.store_urlWithoutDoubleProtocol`
- 单元测试：`UrlNormalizerTest`（coverImage 双斜杠）
- 手动：配置 `access-url-prefix` 为 `http://http://host:port/` 形式，上传后检查返回 URL 无重复协议

## 2. 会议时长 / 规模 / 举办频率（已实现）

**修复**：模板增加 `meetingDuration`、`meetingScale`、`frequency` 字段；新增 `GET /api/dictionaries` 返回对应下拉选项。

**回归**：创建/编辑模板可传上述字段；`GET /api/dictionaries` 返回 meetingDurations、meetingScales、frequencies。

## 3. 目标人群 targetAudience 类型

**修复**：`MeetingTemplateDTO` 使用 `@JsonSetter` 支持数字、字符串两种入参。

**回归**：
- 请求体 `"targetAudience": 1` 或 `"targetAudience": "1"` 均可正确保存

## 4. 新模板默认状态

**修复**：`MeetingTemplateUseCase.create` 默认 `isActive=false`（未上架）。

**回归**：
- 单元测试：`MeetingTemplateUseCaseTest.create_defaultIsActiveFalse`

## 5. 模板下架后列表空

**修复**：
- XML 查询增加 `is_deleted=0` 过滤
- 新增 `PATCH /{id}/offline`、`PATCH /{id}/shelve`，下架仅改 `isActive` 不软删

**回归**：
- 下架单模板后，其他模板仍正常返回
- 上架/下架与删除分离，列表只显示 `is_active=1` 且未删除的模板

## 6. 字典数据（已实现）

**修复**：`GET /api/dictionaries` 返回 regions、targetAudiences、developerTypes。

**回归**：创建会议页可调用该接口获取地域、目标人群、开发者类型下拉选项。

## 7. subVenues 可选 + 图片 URL 双斜杠

**修复**：
- `MeetingDomainService.validateAgendaIntegrity` 允许 `subVenues` 为空
- `UrlNormalizer.normalizeImageUrl` 去除路径双斜杠，`MeetingApplicationService` 对 coverImage 做规范化

**回归**：
- 单元测试：`MeetingDomainServiceAgendaTest.SubVenueOptional.emptySubVenues_passes`
- 创建会议时 `sessions[].subVenues` 可为空数组
- coverImage 传入 `http://host:8080//uploads/...` 时存储为单斜杠

## 8. 会议详情 coverImage 缺失、日期格式

**修复**：
- `MeetingDTO` 增加 `posterUrl`，`toMeetingDTO` 同时设置 `coverImage`、`posterUrl`
- `startTime`、`endTime` 增加 `@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Shanghai")`

**回归**：
- `GET /api/meetings/{id}` 响应含 `coverImage`、`posterUrl`
- 日期格式为 `yyyy-MM-dd'T'HH:mm:ss`

## 9. 热门标签接口（已实现）

**修复**：`GET /api/tags/hot?limit=20` 返回按使用该标签的已发布会议数降序的热门标签。

**回归**：调用接口可获取热门标签列表，用于创建会议/筛选等场景。
