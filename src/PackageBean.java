/**
 * 数据包
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/24
 */
public class PackageBean {


    /**
     * 用户id
     */
    Long userId;
    /**
     * 包类型
     */
    Integer type;

    /**
     * 包顺序
     */
    Long index;

    /**
     * 包总数
     */
    Long total;

    /**
     * 包内容
     */
    String content;

    public long getUserId(){
        return userId;
    }

    public void setUserId(long userId){
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    /**
     * 1 心跳
     * 2 请求连接
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}