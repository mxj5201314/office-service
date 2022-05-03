package com.zhouzhao.office.online_collaborative_office.common.config.xss;

import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);

        if (StringUtils.isNotBlank(parameter)) {
            parameter = HtmlUtil.filter(parameter);
        }

        return parameter;
    }


    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        if (parameterValues!=null){
            for (int i = 0; i < parameterValues.length; i++) {

                if (StringUtils.isNotBlank(parameterValues[i])) {
                    parameterValues[i] = HtmlUtil.filter(parameterValues[i]);
                }
            }
        }

        return parameterValues;
    }

    @Override
    public Map<String, String[]> getParameterMap() {

        Map<String, String[]> parameterMap = super.getParameterMap();
        if (parameterMap != null) {

            for (Map.Entry<String, String[]> stringEntry : parameterMap.entrySet()) {
                String[] value = stringEntry.getValue();
                for (int i = 0; i < value.length; i++) {
                    if (StringUtils.isNotBlank(value[i])) {
                        value[i] = HtmlUtil.filter(value[i]);
                    }
                }
                stringEntry.setValue(value);
            }
        }
        return parameterMap;
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream inputStream = super.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder stringBuilder = new StringBuilder();

        String line = bufferedReader.readLine();

        while (line != null) {
            stringBuilder.append(line);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();

        String s = stringBuilder.toString();
        if (StringUtils.isNotEmpty(s)) {
            Map<String, Object> parameterMap = JSONUtil.parseObj(s);
            if (parameterMap != null) {
                for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        value = HtmlUtil.filter((String) value);
                    }
                    entry.setValue(value);
                }
                s = JSONUtil.toJsonStr(parameterMap);
            }
        }
        ByteArrayInputStream bain = new ByteArrayInputStream(s.getBytes());



        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bain.read();
            }

            @Override
            public boolean isFinished() {

                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
