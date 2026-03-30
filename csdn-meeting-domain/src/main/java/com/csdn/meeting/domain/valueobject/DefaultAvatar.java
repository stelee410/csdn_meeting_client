package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认头像枚举
 * 提供系统预置的默认头像列表供用户选择
 */
@Getter
public enum DefaultAvatar {

    /**
     * 默认头像1 - 蓝色技术风格
     */
    AVATAR_1("https://placehold.co/100x100/3B82F6/FFFFFF/png?text=U1", "科技蓝", 1),

    /**
     * 默认头像2 - 绿色自然风格
     */
    AVATAR_2("https://placehold.co/100x100/10B981/FFFFFF/png?text=U2", "自然绿", 2),

    /**
     * 默认头像3 - 橙色活力风格
     */
    AVATAR_3("https://placehold.co/100x100/F59E0B/FFFFFF/png?text=U3", "活力橙", 3),

    /**
     * 默认头像4 - 紫色创意风格
     */
    AVATAR_4("https://placehold.co/100x100/8B5CF6/FFFFFF/png?text=U4", "创意紫", 4),

    /**
     * 默认头像5 - 灰色商务风格
     */
    AVATAR_5("https://placehold.co/100x100/6B7280/FFFFFF/png?text=U5", "商务灰", 5);

    private final String url;
    private final String displayName;
    private final int code;

    DefaultAvatar(String url, String displayName, int code) {
        this.url = url;
        this.displayName = displayName;
        this.code = code;
    }

    /**
     * 获取所有默认头像列表
     *
     * @return 默认头像列表
     */
    public static List<DefaultAvatar> getAllAvatars() {
        return Arrays.asList(values());
    }

    /**
     * 获取所有默认头像URL列表
     *
     * @return URL列表
     */
    public static List<String> getAllAvatarUrls() {
        return Arrays.stream(values())
                .map(DefaultAvatar::getUrl)
                .collect(Collectors.toList());
    }

    /**
     * 根据code获取默认头像
     *
     * @param code 头像编号
     * @return 默认头像，找不到返回null
     */
    public static DefaultAvatar of(Integer code) {
        if (code == null) {
            return null;
        }
        for (DefaultAvatar avatar : values()) {
            if (avatar.code == code) {
                return avatar;
            }
        }
        return null;
    }

    /**
     * 根据URL获取默认头像
     *
     * @param url 头像URL
     * @return 默认头像，找不到返回null
     */
    public static DefaultAvatar of(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        for (DefaultAvatar avatar : values()) {
            if (avatar.url.equals(url)) {
                return avatar;
            }
        }
        return null;
    }

    /**
     * 随机获取一个默认头像
     * 用于用户未选择头像时的默认值
     *
     * @return 默认头像
     */
    public static DefaultAvatar getRandom() {
        DefaultAvatar[] values = values();
        int index = (int) (Math.random() * values.length);
        return values[index];
    }

    /**
     * 检查URL是否是系统默认头像
     *
     * @param url 头像URL
     * @return 是否是默认头像
     */
    public static boolean isDefaultAvatar(String url) {
        return of(url) != null;
    }
}
