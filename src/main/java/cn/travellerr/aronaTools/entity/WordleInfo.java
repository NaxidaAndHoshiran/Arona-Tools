package cn.travellerr.aronaTools.entity;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Table
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordleInfo {
    @Id
    private int id;

    private Long userId;

    private String name;

    private int difficulty;

    private int wordleScore;

    private Long wordleTime;

    @Builder.Default
    private Date completeDate = new Date();

    private boolean isGroup;

    public String getWordleRank() {
        return (this.isGroup ? "群聊：" : "用户：") + this.name
                + "\n难度：" + this.transformDifficulty()
                +"\n分数：" + this.wordleScore
                + "\n用时：" + DateUtil.formatBetween(this.wordleTime, BetweenFormatter.Level.SECOND)
                + "\n完成时间：" + DateUtil.format(this.completeDate, "yyyy-MM-dd HH:mm:ss");
    }

    private String transformDifficulty() {
        return switch (this.difficulty) {
            case 1 -> "简单";
            case 3 -> "困难";
            default -> "中等";
        };
    }

}
