package cn.travellerr.aronaTools.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Table
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncInfo {
    @Id
    private Long userId;

    private Long targetId;

    private boolean isSynced;
}
