package com.jsdc.worktime.global;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeComScheduleClient {
    private static final String BASE_URL = "https://qyapi.weixin.qq.com/cgi-bin";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取企业微信Access Token
     * @param corpid 企业ID
     * @param corpsecret 应用密钥
     * @return Access Token
     */
    public static String getAccessToken(String corpid, String corpsecret) throws IOException {
        String url = String.format("%s/gettoken?corpid=%s&corpsecret=%s", BASE_URL, corpid, corpsecret);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                Map<String, Object> result = objectMapper.readValue(response.getEntity().getContent(), Map.class);
                if ((Integer) result.get("errcode") == 0) {
                    return (String) result.get("access_token");
                } else {
                    throw new IOException("获取Access Token失败: " + result.get("errmsg"));
                }
            }
        }
    }

    /**
     * 获取日程列表
     * @param accessToken Access Token
     * @param calId 日历ID
     * @param startTime 开始时间（ISO 8601格式：yyyy-MM-dd'T'HH:mm:ss+08:00）
     * @param endTime 结束时间（同上）
     * @return 日程列表
     */
    public static ScheduleListResponse getScheduleList(String accessToken, String calId,
                                                       String startTime, String endTime) throws IOException {
        String url = String.format("%s/schedule/list?access_token=%s", BASE_URL, accessToken);

        // 构造请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("cal_id", calId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(params), ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return objectMapper.readValue(response.getEntity().getContent(), ScheduleListResponse.class);
            }
        }
    }
}