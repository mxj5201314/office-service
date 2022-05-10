package com.zhouzhao.office.online_collaborative_office.common.components;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class BaiDuFaceHandler {
    @Autowired
    private AipFace aipFace;

    @Value("${baidu.face.groupId}")
    private String groupId;

    public final static String IMAGE_TYPE_URl = "URl";

    public final static String IMAGE_TYPE_BASE64 = "BASE64";

    //活体检测控制
    public final static String LIVENESS_CONTROL_LOW = "LOW";

    //图片质量控制
    public final static String QUALITY_CONTROL_NORMAL = "LOW";

    private final static String SUCCESS = "SUCCESS";

    public String faceRegister(Integer id, String liveness_control, String imageType, String image) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<>();

        options.put("liveness_control", liveness_control);
        // 人脸注册
        JSONObject res = aipFace.addUser(image, imageType, groupId, id+"", options);
        System.out.println(res.toString(2));

        return res.getJSONObject("result").getString("face_token");
    }

    public Boolean faceIsExist(Integer id, String liveness_control, String imageType, String image) {
        // 传入可选参数调用接口
        HashMap<String, Object> options = new HashMap<>();
        //Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@90cdf23]
        //{
        //  "result": null,
        //  "log_id": 2838245704,
        //  "error_msg": "match user is not found",
        //  "cached": 0,
        //  "error_code": 222207,
        //  "timestamp": 1651250838
        //}

        options.put("quality_control", QUALITY_CONTROL_NORMAL);
        options.put("liveness_control", liveness_control);
        options.put("user_id", id);
        // 人脸搜索
        try {
            JSONObject res = aipFace.search(image, imageType, groupId, options);
            System.out.println(res.toString(2));
            if (SUCCESS.equals(res.getString("error_msg"))) {
                return res.getJSONObject("result").getJSONArray("user_list").getJSONObject(0).getDouble("score") >= 80;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
