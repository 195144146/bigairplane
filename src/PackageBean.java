import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

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
    Object content;

    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId){
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
        if(type == 1){
            setIndex(1L);
            setTotal(1L);
            Map<String,Object> map = new HashMap<>();
            map.put("status",(byte) 1);
            try {
                setContent(new ObjectMapper().writeValueAsString(map));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
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

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

}