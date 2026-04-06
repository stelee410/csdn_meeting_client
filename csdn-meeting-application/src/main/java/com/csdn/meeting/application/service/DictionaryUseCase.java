package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.DictionaryDTO;
import com.csdn.meeting.domain.entity.Tag;
import com.csdn.meeting.domain.repository.TagRepository;
import com.csdn.meeting.infrastructure.po.DictionaryPO;
import com.csdn.meeting.infrastructure.repository.mapper.DictionaryPOMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典/下拉选项 UseCase
 * V14 起：flat 类型选项从 t_dictionary 表读取，地域仍保持静态（层级结构较复杂）。
 * 会议标签从 t_tag 表读取。
 */
@Service
public class DictionaryUseCase {

    private final DictionaryPOMapper dictionaryPOMapper;
    private final TagRepository tagRepository;

    public DictionaryUseCase(DictionaryPOMapper dictionaryPOMapper, TagRepository tagRepository) {
        this.dictionaryPOMapper = dictionaryPOMapper;
        this.tagRepository = tagRepository;
    }

    public DictionaryDTO getCreateMeetingDictionaries() {
        DictionaryDTO dto = new DictionaryDTO();
        dto.setMeetingDurations(loadOptions("meeting_duration"));
        dto.setMeetingScales(loadOptions("meeting_scale"));
        dto.setFrequencies(loadOptions("meeting_frequency"));
        dto.setRegions(getRegionOptions());
        dto.setTargetAudiences(loadOptions("target_audience"));
        dto.setMeetingTags(getMeetingTagOptions());
        dto.setDeveloperTypes(loadOptions("developer_type"));
        dto.setOrganizers(loadOptions("organizer"));
        dto.setSceneIndustries(loadOptions("scene_industry"));
        dto.setSceneMarketingRegions(loadOptions("scene_marketing_region"));
        dto.setSceneUniversities(loadOptions("scene_university"));
        return dto;
    }

    /**
     * 从 t_tag 表查询会议标签选项
     */
    private List<DictionaryDTO.Option> getMeetingTagOptions() {
        List<Tag> tags = tagRepository.findAll();
        if (tags == null || tags.isEmpty()) {
            return loadOptions("meeting_tag");
        }
        return tags.stream()
                .map(tag -> new DictionaryDTO.Option(
                        String.valueOf(tag.getId()),
                        tag.getTagName()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 从 t_dictionary 按分类读取启用项并转换为 Option 列表。
     */
    private List<DictionaryDTO.Option> loadOptions(String dictType) {
        List<DictionaryPO> items = dictionaryPOMapper.selectActiveByType(dictType);
        return items.stream()
                .map(po -> new DictionaryDTO.Option(po.getItemCode(), po.getItemLabel()))
                .collect(Collectors.toList());
    }

    /**
     * 会议举办地域（省/市），覆盖全国所有省级行政区及地级市。
     */
    public List<DictionaryDTO.RegionOption> getRegionOptions() {
        return Arrays.asList(
                // 直辖市
                new DictionaryDTO.RegionOption("110000", "北京市",
                        Collections.singletonList(new DictionaryDTO.CityOption("110100", "北京市"))),
                new DictionaryDTO.RegionOption("120000", "天津市",
                        Collections.singletonList(new DictionaryDTO.CityOption("120100", "天津市"))),
                new DictionaryDTO.RegionOption("310000", "上海市",
                        Collections.singletonList(new DictionaryDTO.CityOption("310100", "上海市"))),
                new DictionaryDTO.RegionOption("500000", "重庆市",
                        Collections.singletonList(new DictionaryDTO.CityOption("500100", "重庆市"))),
                // 华北
                new DictionaryDTO.RegionOption("130000", "河北省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("130100", "石家庄市"),
                                new DictionaryDTO.CityOption("130200", "唐山市"),
                                new DictionaryDTO.CityOption("130300", "秦皇岛市"),
                                new DictionaryDTO.CityOption("130400", "邯郸市"),
                                new DictionaryDTO.CityOption("130500", "邢台市"),
                                new DictionaryDTO.CityOption("130600", "保定市"),
                                new DictionaryDTO.CityOption("130700", "张家口市"),
                                new DictionaryDTO.CityOption("130800", "承德市"),
                                new DictionaryDTO.CityOption("130900", "沧州市"),
                                new DictionaryDTO.CityOption("131000", "廊坊市"),
                                new DictionaryDTO.CityOption("131100", "衡水市")
                        )),
                new DictionaryDTO.RegionOption("140000", "山西省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("140100", "太原市"),
                                new DictionaryDTO.CityOption("140200", "大同市"),
                                new DictionaryDTO.CityOption("140300", "阳泉市"),
                                new DictionaryDTO.CityOption("140400", "长治市"),
                                new DictionaryDTO.CityOption("140500", "晋城市"),
                                new DictionaryDTO.CityOption("140600", "朔州市"),
                                new DictionaryDTO.CityOption("140700", "晋中市"),
                                new DictionaryDTO.CityOption("140800", "运城市"),
                                new DictionaryDTO.CityOption("140900", "忻州市"),
                                new DictionaryDTO.CityOption("141000", "临汾市"),
                                new DictionaryDTO.CityOption("141100", "吕梁市")
                        )),
                new DictionaryDTO.RegionOption("150000", "内蒙古自治区",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("150100", "呼和浩特市"),
                                new DictionaryDTO.CityOption("150200", "包头市"),
                                new DictionaryDTO.CityOption("150300", "乌海市"),
                                new DictionaryDTO.CityOption("150400", "赤峰市"),
                                new DictionaryDTO.CityOption("150500", "通辽市"),
                                new DictionaryDTO.CityOption("150600", "鄂尔多斯市"),
                                new DictionaryDTO.CityOption("150700", "呼伦贝尔市"),
                                new DictionaryDTO.CityOption("150800", "巴彦淖尔市"),
                                new DictionaryDTO.CityOption("150900", "乌兰察布市")
                        )),
                // 东北
                new DictionaryDTO.RegionOption("210000", "辽宁省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("210100", "沈阳市"),
                                new DictionaryDTO.CityOption("210200", "大连市"),
                                new DictionaryDTO.CityOption("210300", "鞍山市"),
                                new DictionaryDTO.CityOption("210400", "抚顺市"),
                                new DictionaryDTO.CityOption("210500", "本溪市"),
                                new DictionaryDTO.CityOption("210600", "丹东市"),
                                new DictionaryDTO.CityOption("210700", "锦州市"),
                                new DictionaryDTO.CityOption("210800", "营口市"),
                                new DictionaryDTO.CityOption("210900", "阜新市"),
                                new DictionaryDTO.CityOption("211000", "辽阳市"),
                                new DictionaryDTO.CityOption("211100", "盘锦市"),
                                new DictionaryDTO.CityOption("211200", "铁岭市"),
                                new DictionaryDTO.CityOption("211300", "朝阳市"),
                                new DictionaryDTO.CityOption("211400", "葫芦岛市")
                        )),
                new DictionaryDTO.RegionOption("220000", "吉林省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("220100", "长春市"),
                                new DictionaryDTO.CityOption("220200", "吉林市"),
                                new DictionaryDTO.CityOption("220300", "四平市"),
                                new DictionaryDTO.CityOption("220400", "辽源市"),
                                new DictionaryDTO.CityOption("220500", "通化市"),
                                new DictionaryDTO.CityOption("220600", "白山市"),
                                new DictionaryDTO.CityOption("220700", "松原市"),
                                new DictionaryDTO.CityOption("220800", "白城市")
                        )),
                new DictionaryDTO.RegionOption("230000", "黑龙江省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("230100", "哈尔滨市"),
                                new DictionaryDTO.CityOption("230200", "齐齐哈尔市"),
                                new DictionaryDTO.CityOption("230300", "鸡西市"),
                                new DictionaryDTO.CityOption("230400", "鹤岗市"),
                                new DictionaryDTO.CityOption("230500", "双鸭山市"),
                                new DictionaryDTO.CityOption("230600", "大庆市"),
                                new DictionaryDTO.CityOption("230700", "伊春市"),
                                new DictionaryDTO.CityOption("230800", "佳木斯市"),
                                new DictionaryDTO.CityOption("230900", "七台河市"),
                                new DictionaryDTO.CityOption("231000", "牡丹江市"),
                                new DictionaryDTO.CityOption("231100", "黑河市"),
                                new DictionaryDTO.CityOption("231200", "绥化市")
                        )),
                // 华东
                new DictionaryDTO.RegionOption("320000", "江苏省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("320100", "南京市"),
                                new DictionaryDTO.CityOption("320200", "无锡市"),
                                new DictionaryDTO.CityOption("320300", "徐州市"),
                                new DictionaryDTO.CityOption("320400", "常州市"),
                                new DictionaryDTO.CityOption("320500", "苏州市"),
                                new DictionaryDTO.CityOption("320600", "南通市"),
                                new DictionaryDTO.CityOption("320700", "连云港市"),
                                new DictionaryDTO.CityOption("320800", "淮安市"),
                                new DictionaryDTO.CityOption("320900", "盐城市"),
                                new DictionaryDTO.CityOption("321000", "扬州市"),
                                new DictionaryDTO.CityOption("321100", "镇江市"),
                                new DictionaryDTO.CityOption("321200", "泰州市"),
                                new DictionaryDTO.CityOption("321300", "宿迁市")
                        )),
                new DictionaryDTO.RegionOption("330000", "浙江省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("330100", "杭州市"),
                                new DictionaryDTO.CityOption("330200", "宁波市"),
                                new DictionaryDTO.CityOption("330300", "温州市"),
                                new DictionaryDTO.CityOption("330400", "嘉兴市"),
                                new DictionaryDTO.CityOption("330500", "湖州市"),
                                new DictionaryDTO.CityOption("330600", "绍兴市"),
                                new DictionaryDTO.CityOption("330700", "金华市"),
                                new DictionaryDTO.CityOption("330800", "衢州市"),
                                new DictionaryDTO.CityOption("330900", "舟山市"),
                                new DictionaryDTO.CityOption("331000", "台州市"),
                                new DictionaryDTO.CityOption("331100", "丽水市")
                        )),
                new DictionaryDTO.RegionOption("340000", "安徽省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("340100", "合肥市"),
                                new DictionaryDTO.CityOption("340200", "芜湖市"),
                                new DictionaryDTO.CityOption("340300", "蚌埠市"),
                                new DictionaryDTO.CityOption("340400", "淮南市"),
                                new DictionaryDTO.CityOption("340500", "马鞍山市"),
                                new DictionaryDTO.CityOption("340600", "淮北市"),
                                new DictionaryDTO.CityOption("340700", "铜陵市"),
                                new DictionaryDTO.CityOption("340800", "安庆市"),
                                new DictionaryDTO.CityOption("341000", "黄山市"),
                                new DictionaryDTO.CityOption("341100", "滁州市"),
                                new DictionaryDTO.CityOption("341200", "阜阳市"),
                                new DictionaryDTO.CityOption("341300", "宿州市"),
                                new DictionaryDTO.CityOption("341500", "六安市"),
                                new DictionaryDTO.CityOption("341600", "亳州市"),
                                new DictionaryDTO.CityOption("341700", "池州市"),
                                new DictionaryDTO.CityOption("341800", "宣城市")
                        )),
                new DictionaryDTO.RegionOption("350000", "福建省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("350100", "福州市"),
                                new DictionaryDTO.CityOption("350200", "厦门市"),
                                new DictionaryDTO.CityOption("350300", "莆田市"),
                                new DictionaryDTO.CityOption("350400", "三明市"),
                                new DictionaryDTO.CityOption("350500", "泉州市"),
                                new DictionaryDTO.CityOption("350600", "漳州市"),
                                new DictionaryDTO.CityOption("350700", "南平市"),
                                new DictionaryDTO.CityOption("350800", "龙岩市"),
                                new DictionaryDTO.CityOption("350900", "宁德市")
                        )),
                new DictionaryDTO.RegionOption("360000", "江西省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("360100", "南昌市"),
                                new DictionaryDTO.CityOption("360200", "景德镇市"),
                                new DictionaryDTO.CityOption("360300", "萍乡市"),
                                new DictionaryDTO.CityOption("360400", "九江市"),
                                new DictionaryDTO.CityOption("360500", "新余市"),
                                new DictionaryDTO.CityOption("360600", "鹰潭市"),
                                new DictionaryDTO.CityOption("360700", "赣州市"),
                                new DictionaryDTO.CityOption("360800", "吉安市"),
                                new DictionaryDTO.CityOption("360900", "宜春市"),
                                new DictionaryDTO.CityOption("361000", "抚州市"),
                                new DictionaryDTO.CityOption("361100", "上饶市")
                        )),
                new DictionaryDTO.RegionOption("370000", "山东省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("370100", "济南市"),
                                new DictionaryDTO.CityOption("370200", "青岛市"),
                                new DictionaryDTO.CityOption("370300", "淄博市"),
                                new DictionaryDTO.CityOption("370400", "枣庄市"),
                                new DictionaryDTO.CityOption("370500", "东营市"),
                                new DictionaryDTO.CityOption("370600", "烟台市"),
                                new DictionaryDTO.CityOption("370700", "潍坊市"),
                                new DictionaryDTO.CityOption("370800", "济宁市"),
                                new DictionaryDTO.CityOption("370900", "泰安市"),
                                new DictionaryDTO.CityOption("371000", "威海市"),
                                new DictionaryDTO.CityOption("371100", "日照市"),
                                new DictionaryDTO.CityOption("371300", "临沂市"),
                                new DictionaryDTO.CityOption("371400", "德州市"),
                                new DictionaryDTO.CityOption("371500", "聊城市"),
                                new DictionaryDTO.CityOption("371600", "滨州市"),
                                new DictionaryDTO.CityOption("371700", "菏泽市")
                        )),
                // 华中
                new DictionaryDTO.RegionOption("410000", "河南省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("410100", "郑州市"),
                                new DictionaryDTO.CityOption("410200", "开封市"),
                                new DictionaryDTO.CityOption("410300", "洛阳市"),
                                new DictionaryDTO.CityOption("410400", "平顶山市"),
                                new DictionaryDTO.CityOption("410500", "安阳市"),
                                new DictionaryDTO.CityOption("410600", "鹤壁市"),
                                new DictionaryDTO.CityOption("410700", "新乡市"),
                                new DictionaryDTO.CityOption("410800", "焦作市"),
                                new DictionaryDTO.CityOption("410900", "濮阳市"),
                                new DictionaryDTO.CityOption("411000", "许昌市"),
                                new DictionaryDTO.CityOption("411100", "漯河市"),
                                new DictionaryDTO.CityOption("411200", "三门峡市"),
                                new DictionaryDTO.CityOption("411300", "南阳市"),
                                new DictionaryDTO.CityOption("411400", "商丘市"),
                                new DictionaryDTO.CityOption("411500", "信阳市"),
                                new DictionaryDTO.CityOption("411600", "周口市"),
                                new DictionaryDTO.CityOption("411700", "驻马店市")
                        )),
                new DictionaryDTO.RegionOption("420000", "湖北省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("420100", "武汉市"),
                                new DictionaryDTO.CityOption("420200", "黄石市"),
                                new DictionaryDTO.CityOption("420300", "十堰市"),
                                new DictionaryDTO.CityOption("420500", "宜昌市"),
                                new DictionaryDTO.CityOption("420600", "襄阳市"),
                                new DictionaryDTO.CityOption("420700", "鄂州市"),
                                new DictionaryDTO.CityOption("420800", "荆门市"),
                                new DictionaryDTO.CityOption("420900", "孝感市"),
                                new DictionaryDTO.CityOption("421000", "荆州市"),
                                new DictionaryDTO.CityOption("421100", "黄冈市"),
                                new DictionaryDTO.CityOption("421200", "咸宁市"),
                                new DictionaryDTO.CityOption("421300", "随州市")
                        )),
                new DictionaryDTO.RegionOption("430000", "湖南省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("430100", "长沙市"),
                                new DictionaryDTO.CityOption("430200", "株洲市"),
                                new DictionaryDTO.CityOption("430300", "湘潭市"),
                                new DictionaryDTO.CityOption("430400", "衡阳市"),
                                new DictionaryDTO.CityOption("430500", "邵阳市"),
                                new DictionaryDTO.CityOption("430600", "岳阳市"),
                                new DictionaryDTO.CityOption("430700", "常德市"),
                                new DictionaryDTO.CityOption("430800", "张家界市"),
                                new DictionaryDTO.CityOption("430900", "益阳市"),
                                new DictionaryDTO.CityOption("431000", "郴州市"),
                                new DictionaryDTO.CityOption("431100", "永州市"),
                                new DictionaryDTO.CityOption("431200", "怀化市"),
                                new DictionaryDTO.CityOption("431300", "娄底市")
                        )),
                // 华南
                new DictionaryDTO.RegionOption("440000", "广东省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("440100", "广州市"),
                                new DictionaryDTO.CityOption("440200", "韶关市"),
                                new DictionaryDTO.CityOption("440300", "深圳市"),
                                new DictionaryDTO.CityOption("440400", "珠海市"),
                                new DictionaryDTO.CityOption("440500", "汕头市"),
                                new DictionaryDTO.CityOption("440600", "佛山市"),
                                new DictionaryDTO.CityOption("440700", "江门市"),
                                new DictionaryDTO.CityOption("440800", "湛江市"),
                                new DictionaryDTO.CityOption("440900", "茂名市"),
                                new DictionaryDTO.CityOption("441200", "肇庆市"),
                                new DictionaryDTO.CityOption("441300", "惠州市"),
                                new DictionaryDTO.CityOption("441400", "梅州市"),
                                new DictionaryDTO.CityOption("441500", "汕尾市"),
                                new DictionaryDTO.CityOption("441600", "河源市"),
                                new DictionaryDTO.CityOption("441700", "阳江市"),
                                new DictionaryDTO.CityOption("441800", "清远市"),
                                new DictionaryDTO.CityOption("441900", "东莞市"),
                                new DictionaryDTO.CityOption("442000", "中山市"),
                                new DictionaryDTO.CityOption("445100", "潮州市"),
                                new DictionaryDTO.CityOption("445200", "揭州市"),
                                new DictionaryDTO.CityOption("445300", "云浮市")
                        )),
                new DictionaryDTO.RegionOption("450000", "广西壮族自治区",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("450100", "南宁市"),
                                new DictionaryDTO.CityOption("450200", "柳州市"),
                                new DictionaryDTO.CityOption("450300", "桂林市"),
                                new DictionaryDTO.CityOption("450400", "梧州市"),
                                new DictionaryDTO.CityOption("450500", "北海市"),
                                new DictionaryDTO.CityOption("450600", "防城港市"),
                                new DictionaryDTO.CityOption("450700", "钦州市"),
                                new DictionaryDTO.CityOption("450800", "贵港市"),
                                new DictionaryDTO.CityOption("450900", "玉林市"),
                                new DictionaryDTO.CityOption("451000", "百色市"),
                                new DictionaryDTO.CityOption("451100", "贺州市"),
                                new DictionaryDTO.CityOption("451200", "河池市"),
                                new DictionaryDTO.CityOption("451300", "来宾市"),
                                new DictionaryDTO.CityOption("451400", "崇左市")
                        )),
                new DictionaryDTO.RegionOption("460000", "海南省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("460100", "海口市"),
                                new DictionaryDTO.CityOption("460200", "三亚市"),
                                new DictionaryDTO.CityOption("460400", "儋州市")
                        )),
                // 西南
                new DictionaryDTO.RegionOption("510000", "四川省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("510100", "成都市"),
                                new DictionaryDTO.CityOption("510300", "自贡市"),
                                new DictionaryDTO.CityOption("510400", "攀枝花市"),
                                new DictionaryDTO.CityOption("510500", "泸州市"),
                                new DictionaryDTO.CityOption("510600", "德阳市"),
                                new DictionaryDTO.CityOption("510700", "绵阳市"),
                                new DictionaryDTO.CityOption("510800", "广元市"),
                                new DictionaryDTO.CityOption("510900", "遂宁市"),
                                new DictionaryDTO.CityOption("511000", "内江市"),
                                new DictionaryDTO.CityOption("511100", "乐山市"),
                                new DictionaryDTO.CityOption("511300", "南充市"),
                                new DictionaryDTO.CityOption("511400", "眉山市"),
                                new DictionaryDTO.CityOption("511500", "宜宾市"),
                                new DictionaryDTO.CityOption("511600", "广安市"),
                                new DictionaryDTO.CityOption("511700", "达州市"),
                                new DictionaryDTO.CityOption("511800", "雅安市"),
                                new DictionaryDTO.CityOption("511900", "巴中市"),
                                new DictionaryDTO.CityOption("512000", "资阳市")
                        )),
                new DictionaryDTO.RegionOption("520000", "贵州省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("520100", "贵阳市"),
                                new DictionaryDTO.CityOption("520200", "六盘水市"),
                                new DictionaryDTO.CityOption("520300", "遵义市"),
                                new DictionaryDTO.CityOption("520400", "安顺市"),
                                new DictionaryDTO.CityOption("520500", "毕节市"),
                                new DictionaryDTO.CityOption("520600", "铜仁市")
                        )),
                new DictionaryDTO.RegionOption("530000", "云南省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("530100", "昆明市"),
                                new DictionaryDTO.CityOption("530300", "曲靖市"),
                                new DictionaryDTO.CityOption("530400", "玉溪市"),
                                new DictionaryDTO.CityOption("530500", "保山市"),
                                new DictionaryDTO.CityOption("530600", "昭通市"),
                                new DictionaryDTO.CityOption("530700", "丽江市"),
                                new DictionaryDTO.CityOption("530800", "普洱市"),
                                new DictionaryDTO.CityOption("530900", "临沧市")
                        )),
                new DictionaryDTO.RegionOption("540000", "西藏自治区",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("540100", "拉萨市"),
                                new DictionaryDTO.CityOption("540200", "日喀则市"),
                                new DictionaryDTO.CityOption("540300", "昌都市"),
                                new DictionaryDTO.CityOption("540400", "林芝市"),
                                new DictionaryDTO.CityOption("540500", "山南市"),
                                new DictionaryDTO.CityOption("540600", "那曲市")
                        )),
                // 西北
                new DictionaryDTO.RegionOption("610000", "陕西省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("610100", "西安市"),
                                new DictionaryDTO.CityOption("610200", "铜川市"),
                                new DictionaryDTO.CityOption("610300", "宝鸡市"),
                                new DictionaryDTO.CityOption("610400", "咸阳市"),
                                new DictionaryDTO.CityOption("610500", "渭南市"),
                                new DictionaryDTO.CityOption("610600", "延安市"),
                                new DictionaryDTO.CityOption("610700", "汉中市"),
                                new DictionaryDTO.CityOption("610800", "榆林市"),
                                new DictionaryDTO.CityOption("610900", "安康市"),
                                new DictionaryDTO.CityOption("611000", "商洛市")
                        )),
                new DictionaryDTO.RegionOption("620000", "甘肃省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("620100", "兰州市"),
                                new DictionaryDTO.CityOption("620200", "嘉峪关市"),
                                new DictionaryDTO.CityOption("620300", "金昌市"),
                                new DictionaryDTO.CityOption("620400", "白银市"),
                                new DictionaryDTO.CityOption("620500", "天水市"),
                                new DictionaryDTO.CityOption("620600", "武威市"),
                                new DictionaryDTO.CityOption("620700", "张掖市"),
                                new DictionaryDTO.CityOption("620800", "平凉市"),
                                new DictionaryDTO.CityOption("620900", "酒泉市"),
                                new DictionaryDTO.CityOption("621000", "庆阳市"),
                                new DictionaryDTO.CityOption("621100", "定西市"),
                                new DictionaryDTO.CityOption("621200", "陇南市")
                        )),
                new DictionaryDTO.RegionOption("630000", "青海省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("630100", "西宁市"),
                                new DictionaryDTO.CityOption("630200", "海东市")
                        )),
                new DictionaryDTO.RegionOption("640000", "宁夏回族自治区",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("640100", "银川市"),
                                new DictionaryDTO.CityOption("640200", "石嘴山市"),
                                new DictionaryDTO.CityOption("640300", "吴忠市"),
                                new DictionaryDTO.CityOption("640400", "固原市"),
                                new DictionaryDTO.CityOption("640500", "中卫市")
                        )),
                new DictionaryDTO.RegionOption("650000", "新疆维吾尔自治区",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("650100", "乌鲁木齐市"),
                                new DictionaryDTO.CityOption("650200", "克拉玛依市"),
                                new DictionaryDTO.CityOption("650400", "吐鲁番市"),
                                new DictionaryDTO.CityOption("650500", "哈密市")
                        )),
                // 港澳台
                new DictionaryDTO.RegionOption("710000", "台湾省",
                        Collections.singletonList(new DictionaryDTO.CityOption("710100", "台北市"))),
                new DictionaryDTO.RegionOption("810000", "香港特别行政区",
                        Collections.singletonList(new DictionaryDTO.CityOption("810100", "香港"))),
                new DictionaryDTO.RegionOption("820000", "澳门特别行政区",
                        Collections.singletonList(new DictionaryDTO.CityOption("820100", "澳门")))
        );
    }
}
