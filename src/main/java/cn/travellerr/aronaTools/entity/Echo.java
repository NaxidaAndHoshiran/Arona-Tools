package cn.travellerr.aronaTools.entity;

import cn.chahuyun.hibernateplus.HibernateFactory;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Echo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 回声洞ID

    private Long userId;        // 用户QQ号码

    private String userName;    // 用户昵称

    private String message;     // 消息内容

    @Builder.Default
    private Long groupId = null;       // QQ群号

    @Builder.Default
    private String groupName = null;   // QQ群名称

    @Builder.Default
    private Date createTime = new Date();    // 创建时间

    @Builder.Default
    private Integer readTimes = 0;      // 阅读次数

    @Builder.Default
    private Boolean isApproved = true; // 是否审核通过

    @Builder.Default
    private Boolean isReported = false; // 是否被举报

    public String buildMessage() {
        return "回声洞ID: " + this.id + "\n\n" +
                this.message + "\n" +
                "by " + this.userName + " (" + this.userId + ")\n" +
                "创建时间: " + this.createTime + "\n" +
                "回声次数: " + this.readTimes + "\n";
    }

    public void addReadTimes() {
        this.readTimes++;
        HibernateFactory.merge(this);
    }
}

