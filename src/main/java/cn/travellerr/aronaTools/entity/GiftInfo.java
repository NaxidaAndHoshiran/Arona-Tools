package cn.travellerr.aronaTools.entity;

import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Table
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftInfo implements Serializable {

    @Id
    private Integer giftId;

    private String giftName;

    private String giftDescription;

    private String receivedGiftMessage;

    /**
     * 礼包包含的物品id与数量，key为物品id，value为数量
     * {"1":1,"2":2}
     *
     *
     */
    private String contain;


    @Lob
    private String receivers;

    @Transient
    @Builder.Default
    private List<Long> receiversList = new ArrayList<>();

    public static String mapToString(Map<String, Double> map) {
        return new JSONObject(map).toString();
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public Map<String, Double> getContain() {
        Map<String, Double> map = new HashMap<>();
        final JSONObject jsonObject = new JSONObject(contain == null || contain.isEmpty() ? "{}" : contain);

        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.getDouble(key));
        }
        return map;
    }

    public void setContain(Map<String, Double> contain) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(contain);
        //Log.info("setContain: " + jsonObject);
        this.contain = jsonObject.toString();
    }

    public List<Long> getReceivers() {
        this.receiversList = new ArrayList<>();
        if (StrUtil.isNotBlank(receivers)) {
            for (String s : receivers.split(",")) {
                receiversList.add(Long.parseLong(s));
            }
        }
        return receiversList;
    }

    public void setReceivers(List<Long> receiverList) {
        this.receiversList = receiverList;
        this.receivers = receiverList.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    public void addReceiver(Long receiver) {
        this.receiversList = getReceivers();
        receiversList.add(receiver);
        this.receivers = receiversList.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        HibernateFactory.merge(this);
    }

    public void removeReceiver(Long receiver) {
        this.receiversList = getReceivers();
        receiversList.remove(receiver);
        this.receivers = receiversList.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    @SuppressWarnings("unused")
    public Integer getGiftId() {
        return giftId;
    }

    public String getGiftInfo() {
        return "礼包名称：" + giftName + "\n" +
                "礼包描述：" + giftDescription + "\n" +
                "礼包内容：" + contain + "\n" +
                "领取信息：" + receivedGiftMessage + "\n" +
                "已领取用户：" + receivers + "\n" +
                "领取总数：" + receivers.split(",").length;

    }

}
