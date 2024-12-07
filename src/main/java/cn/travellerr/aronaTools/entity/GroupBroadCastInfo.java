package cn.travellerr.aronaTools.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Table
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBroadCastInfo {
    @Id
    private Long id;

    private String groupName;

    @Builder.Default
    private Date lastBroadCastTime = new Date();

    @Builder.Default
    private boolean applyUrl = true;

    @Builder.Default
    private boolean isTurnedOnService = true;

    @Builder.Default
    private boolean isBroadCasted = false;


    public void updateLastBroadCastTime() {
        this.lastBroadCastTime = new Date();
    }
}
