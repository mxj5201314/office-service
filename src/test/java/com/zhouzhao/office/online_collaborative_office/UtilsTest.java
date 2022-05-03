package com.zhouzhao.office.online_collaborative_office;


import com.zhouzhao.office.online_collaborative_office.common.utils.WXUtil;
import org.junit.jupiter.api.Test;

public class UtilsTest {

    @Test
    public void test(){
        String openId = WXUtil.getOpenId("wx5a3aefbd83ab240c", "468d43e939f5a39b4e254d8003db1389", "");
        System.out.println("openId = " + openId);
    }
}
