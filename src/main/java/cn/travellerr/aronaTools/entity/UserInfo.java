package cn.travellerr.aronaTools.entity;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @Id
    private Long id;

    private String name;

    private int wordleScore;

    private Long wordleTime;

    private boolean isGroup;

    public String getWordleRank() {
        return (this.isGroup ? "群聊：" : "用户：") + this.name + "\n分数：" + this.wordleScore + "\n用时：" + DateUtil.formatBetween(this.wordleTime, BetweenFormatter.Level.SECOND);
    }

}
