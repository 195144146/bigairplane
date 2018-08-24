/**
 * socket连接类
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/24
 */
public class SocketConnectionBean {

    /**
     * 用户id
     */
    Long userId;

    /**
     * 请求连接用户id
     */
    Long requestUserId;

    /**
     * 请求用户端口
     */
    Integer RequestUserport;

    /**
     * 请求用户地址
     */
    String requestUserNetAddress;

    /**
     * 状态 false不可请求 true 可以请求
     */
    Boolean status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(Long requestUserId) {
        this.requestUserId = requestUserId;
    }

    public Integer getRequestUserport() {
        return RequestUserport;
    }

    public void setRequestUserport(Integer requestUserport) {
        RequestUserport = requestUserport;
    }

    public String getRequestUserNetAddress() {
        return requestUserNetAddress;
    }

    public void setRequestUserNetAddress(String requestUserNetAddress) {
        this.requestUserNetAddress = requestUserNetAddress;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
