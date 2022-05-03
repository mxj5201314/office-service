package com.zhouzhao.office.online_collaborative_office.common.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;

public class EpidemicLevelUtil {
    public static final Integer HEIGHT = 3;
    public static final Integer MIDDLE = 2;
    public static final Integer LOW = 1;




    public static Integer getEpidemicLevel(String district) throws Exception {
        if (district == null) {
            throw new Exception("地区参数为空");
        }

        String res = HttpUtil.get("https://app.gjzwfw.gov.cn/fwmhapp3/yhzx/app/xzqh.do");
        JSONObject jsonObject = JSONObject.parseObject(res);
        String substring = trySubStr(district, "省", "市", "县", "区", "镇", "村", "（地区）乡");
        if (jsonObject.getBoolean("success")) {

            return findLevelByJSON(jsonObject, district, substring);
        }
        throw new Exception("接口查询失败");
    }

    private static String trySubStr(String target, String... values) throws Exception {
        if (target == null) {
            throw new Exception("地区参数为空");
        }

        for (String value : values) {
            if (target.lastIndexOf(value) != -1) {
                target = target.substring(0, target.lastIndexOf(value));
                break;
            }
        }
        return target;
    }

    private static Integer findLevelByJSON(JSONObject jsonObject, String district, String substring) {
        String high = jsonObject.getJSONObject("params").getJSONArray("high").toJSONString();
        String middle = jsonObject.getJSONObject("params").getJSONArray("middle").toJSONString();
        int index = !high.contains('"' + district + '"') ? high.indexOf('"' + substring + '"') : high.indexOf('"' + district + '"');
        if (index != -1) {
            return HEIGHT;
        } else {
            index = !middle.contains('"' + district + '"') ? middle.indexOf('"' + substring + '"') : middle.indexOf('"' + district + '"');
            if (index != -1) {
                return MIDDLE;
            } else {
                return LOW;
            }
        }

    }

}
