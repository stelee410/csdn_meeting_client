package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.FormFieldConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报名表单配置服务
 * 提供报名表单字段的配置管理，支持运营人员通过运维接口动态配置
 * 
 * 前期通过运维接口调整配置，后期可扩展为可视化配置页面
 */
@Service
public class RegistrationConfigUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationConfigUseCase.class);

    // 默认表单配置缓存
    private final List<FormFieldConfigDTO> defaultConfig;

    // 自定义配置缓存（会议ID -> 配置列表）
    private final Map<String, List<FormFieldConfigDTO>> customConfigs = new HashMap<>();

    public RegistrationConfigUseCase() {
        // 初始化默认配置
        this.defaultConfig = initDefaultConfig();
    }

    /**
     * 获取默认表单配置
     * 当会议没有自定义配置时使用
     */
    public List<FormFieldConfigDTO> getDefaultConfig() {
        return new ArrayList<>(defaultConfig);
    }

    /**
     * 获取指定会议的表单配置
     * 优先返回自定义配置，如无则返回默认配置
     * 
     * @param meetingId 会议ID
     * @return 表单字段配置列表
     */
    public List<FormFieldConfigDTO> getConfig(String meetingId) {
        if (meetingId == null || meetingId.isEmpty()) {
            return getDefaultConfig();
        }
        
        // 优先返回自定义配置
        List<FormFieldConfigDTO> customConfig = customConfigs.get(meetingId);
        if (customConfig != null && !customConfig.isEmpty()) {
            logger.info("使用会议 {} 的自定义报名表单配置", meetingId);
            return new ArrayList<>(customConfig);
        }
        
        // 无自定义配置则返回默认配置
        logger.debug("会议 {} 使用默认报名表单配置", meetingId);
        return getDefaultConfig();
    }

    /**
     * 保存会议的自定义表单配置
     * 运营人员通过运维接口调用
     * 
     * @param meetingId 会议ID
     * @param fields 表单字段配置列表
     */
    public void saveConfig(String meetingId, List<FormFieldConfigDTO> fields) {
        if (meetingId == null || meetingId.isEmpty()) {
            throw new IllegalArgumentException("会议ID不能为空");
        }
        
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("表单配置不能为空");
        }
        
        // 校验必填字段
        validateConfig(fields);
        
        // 保存配置
        customConfigs.put(meetingId, new ArrayList<>(fields));
        logger.info("保存会议 {} 的报名表单配置成功，共 {} 个字段", meetingId, fields.size());
    }

    /**
     * 清除会议的自定义配置（恢复使用默认配置）
     * 
     * @param meetingId 会议ID
     */
    public void clearConfig(String meetingId) {
        if (meetingId != null && !meetingId.isEmpty()) {
            customConfigs.remove(meetingId);
            logger.info("清除会议 {} 的自定义报名表单配置", meetingId);
        }
    }

    /**
     * 获取需要从用户画像预填的字段
     * 
     * @param meetingId 会议ID
     * @return 需要预填的字段名称列表
     */
    public List<String> getPreFillableFields(String meetingId) {
        List<FormFieldConfigDTO> config = getConfig(meetingId);
        List<String> preFillableFields = new ArrayList<>();
        
        for (FormFieldConfigDTO field : config) {
            if (Boolean.TRUE.equals(field.getEnabled()) 
                    && "user_profile".equals(field.getSource())) {
                preFillableFields.add(field.getFieldName());
            }
        }
        
        return preFillableFields;
    }

    /**
     * 获取必填字段列表
     * 
     * @param meetingId 会议ID
     * @return 必填字段名称列表
     */
    public List<String> getRequiredFields(String meetingId) {
        List<FormFieldConfigDTO> config = getConfig(meetingId);
        List<String> requiredFields = new ArrayList<>();
        
        for (FormFieldConfigDTO field : config) {
            if (Boolean.TRUE.equals(field.getEnabled()) 
                    && Boolean.TRUE.equals(field.getRequired())) {
                requiredFields.add(field.getFieldName());
            }
        }
        
        return requiredFields;
    }

    /**
     * 验证表单配置的有效性
     */
    private void validateConfig(List<FormFieldConfigDTO> fields) {
        // 检查必填字段
        List<String> requiredFieldNames = Arrays.asList("name", "phone");
        List<String> fieldNames = new ArrayList<>();
        
        for (FormFieldConfigDTO field : fields) {
            // 检查字段名是否重复
            if (fieldNames.contains(field.getFieldName())) {
                throw new IllegalArgumentException("字段名重复: " + field.getFieldName());
            }
            fieldNames.add(field.getFieldName());
            
            // 检查必填字段是否存在且启用
            if (requiredFieldNames.contains(field.getFieldName())) {
                if (!Boolean.TRUE.equals(field.getEnabled())) {
                    throw new IllegalArgumentException("必填字段不能禁用: " + field.getFieldName());
                }
                if (!Boolean.TRUE.equals(field.getRequired())) {
                    throw new IllegalArgumentException("必填字段必须设置为required=true: " + field.getFieldName());
                }
            }
        }
        
        // 检查所有必填字段是否都存在
        for (String requiredField : requiredFieldNames) {
            if (!fieldNames.contains(requiredField)) {
                throw new IllegalArgumentException("缺少必填字段: " + requiredField);
            }
        }
    }

    /**
     * 初始化默认表单配置
     */
    private List<FormFieldConfigDTO> initDefaultConfig() {
        List<FormFieldConfigDTO> config = new ArrayList<>();

        // 姓名 - 必填，从用户画像预填
        FormFieldConfigDTO nameField = new FormFieldConfigDTO();
        nameField.setFieldName("name");
        nameField.setFieldLabel("姓名");
        nameField.setFieldType("TEXT");
        nameField.setRequired(true);
        nameField.setEditable(true);
        nameField.setSource("user_profile");
        nameField.setPlaceholder("请输入您的真实姓名");
        nameField.setSortOrder(1);
        nameField.setEnabled(true);
        config.add(nameField);

        // 手机号 - 必填，从用户画像预填
        FormFieldConfigDTO phoneField = new FormFieldConfigDTO();
        phoneField.setFieldName("phone");
        phoneField.setFieldLabel("手机号");
        phoneField.setFieldType("PHONE");
        phoneField.setRequired(true);
        phoneField.setEditable(true);
        phoneField.setSource("user_profile");
        phoneField.setPlaceholder("用于接收会议通知短信");
        phoneField.setSortOrder(2);
        phoneField.setEnabled(true);
        phoneField.setValidationRegex("^1[3-9]\\d{9}$");
        phoneField.setValidationMessage("请输入有效的手机号");
        config.add(phoneField);

        // 邮箱 - 选填，从用户画像预填
        FormFieldConfigDTO emailField = new FormFieldConfigDTO();
        emailField.setFieldName("email");
        emailField.setFieldLabel("邮箱");
        emailField.setFieldType("EMAIL");
        emailField.setRequired(false);
        emailField.setEditable(true);
        emailField.setSource("user_profile");
        emailField.setPlaceholder("用于接收会议详情邮件");
        emailField.setSortOrder(3);
        emailField.setEnabled(true);
        emailField.setValidationRegex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        emailField.setValidationMessage("请输入有效的邮箱地址");
        config.add(emailField);

        // 公司 - 选填，无预填
        FormFieldConfigDTO companyField = new FormFieldConfigDTO();
        companyField.setFieldName("company");
        companyField.setFieldLabel("公司");
        companyField.setFieldType("TEXT");
        companyField.setRequired(false);
        companyField.setEditable(true);
        companyField.setSource("none");
        companyField.setPlaceholder("请输入您所在的公司");
        companyField.setSortOrder(4);
        companyField.setEnabled(true);
        config.add(companyField);

        // 职位 - 选填，无预填
        FormFieldConfigDTO positionField = new FormFieldConfigDTO();
        positionField.setFieldName("position");
        positionField.setFieldLabel("职位");
        positionField.setFieldType("TEXT");
        positionField.setRequired(false);
        positionField.setEditable(true);
        positionField.setSource("none");
        positionField.setPlaceholder("例如：前端工程师、产品经理等");
        positionField.setSortOrder(5);
        positionField.setEnabled(true);
        config.add(positionField);

        // 所属行业 - 选填，下拉选择
        FormFieldConfigDTO industryField = new FormFieldConfigDTO();
        industryField.setFieldName("industry");
        industryField.setFieldLabel("所属行业");
        industryField.setFieldType("SELECT");
        industryField.setRequired(false);
        industryField.setEditable(true);
        industryField.setSource("none");
        industryField.setPlaceholder("请选择行业");
        industryField.setSortOrder(6);
        industryField.setEnabled(true);
        
        // 行业选项
        List<Map<String, String>> industryOptions = new ArrayList<>();
        addOption(industryOptions, "internet", "互联网/IT");
        addOption(industryOptions, "finance", "金融/银行/保险");
        addOption(industryOptions, "manufacturing", "制造业");
        addOption(industryOptions, "education", "教育/培训");
        addOption(industryOptions, "healthcare", "医疗/健康");
        addOption(industryOptions, "retail", "零售/电商");
        addOption(industryOptions, "real_estate", "房地产/建筑");
        addOption(industryOptions, "media", "传媒/广告");
        addOption(industryOptions, "energy", "能源/环保");
        addOption(industryOptions, "transport", "交通/物流");
        addOption(industryOptions, "government", "政府/公共事业");
        addOption(industryOptions, "other", "其他");
        industryField.setOptions(industryOptions);
        config.add(industryField);

        // 参会目的 - 选填，文本域
        FormFieldConfigDTO purposeField = new FormFieldConfigDTO();
        purposeField.setFieldName("purpose");
        purposeField.setFieldLabel("参会目的");
        purposeField.setFieldType("TEXTAREA");
        purposeField.setRequired(false);
        purposeField.setEditable(true);
        purposeField.setSource("none");
        purposeField.setPlaceholder("简单描述您参加本次会议的目的或期望收获（选填）");
        purposeField.setSortOrder(7);
        purposeField.setEnabled(true);
        config.add(purposeField);

        return config;
    }

    private void addOption(List<Map<String, String>> options, String value, String label) {
        Map<String, String> option = new HashMap<>();
        option.put("value", value);
        option.put("label", label);
        options.add(option);
    }
}
