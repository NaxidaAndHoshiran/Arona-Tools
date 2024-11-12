package cn.travellerr.aronaTools.entity;

import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.travellerr.aronaTools.electronicPets.type.PetType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;

import java.text.DecimalFormat;
import java.util.Date;


/**
 * 宠物信息实体类
 */
@Table
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetInfo {
    /**
     * 用于格式化浮点数的DecimalFormat实例
     */
    @Transient
    private static final DecimalFormat df = new DecimalFormat("0.00");
    /**
     * 用户ID
     */
    @Id
    private Long userId;
    /**
     * 宠物名称
     */
    private String petName;
    /**
     * 宠物类型
     */
    private PetType petType;
    /**
     * 宠物等级
     */
    @Builder.Default
    private Integer petLevel = 0;
    /**
     * 宠物经验值
     */
    @Builder.Default
    private Long petExp = 0L;
    /**
     * 宠物最大经验值
     */
    @Builder.Default
    private Long petMaxExp = 200L;
    /**
     * 宠物当前生命值
     */
    @Builder.Default
    private Double petHp = 100.0;
    /**
     * 宠物最大生命值
     */
    @Builder.Default
    private Integer petMaxHp = 100;
    /**
     * 宠物饥饿值
     */
    @Builder.Default
    private Double petHunger = 100.0;
    /**
     * 宠物最大饥饿值
     */
    @Builder.Default
    private Integer petMaxHunger = 100;
    /**
     * 宠物心情值
     */
    @Builder.Default
    private Double petMood = 100.0;
    /**
     * 宠物最大心情值
     */
    @Builder.Default
    private Integer petMaxMood = 100;
    /**
     * 宠物清洁值
     */
    @Builder.Default
    private double petHealth = 100;
    /**
     * 宠物最大清洁值
     */
    @Builder.Default
    private Integer petMaxHealth = 100;
    /**
     * 宠物能量值
     */
    // TODO 没想好能量值干什么
    @Builder.Default
    private Double petEnergy = 100.0;
    /**
     * 宠物最大能量值
     */
     // TODO 没想好能量值干什么
    @Builder.Default
    private Integer petMaxEnergy = 100;
    /**
     * 宠物好感度
     */
    @Builder.Default
    private Double petRelationship = 0.00;
    /**
     * 宠物是否正在睡觉
     */
    @Builder.Default
    private Boolean isSleeping = false;
    /**
     * 宠物是否生病
     */
    @Builder.Default
    private Boolean isSick = false;
    /**
     * 宠物是否死亡
     */
    @Builder.Default
    private Boolean isDead = false;
    /**
     * 上一次更新时间
     */
    @Builder.Default
    private Date lastUpdateTime = new Date();
    @Builder.Default
    private Boolean isBind = false;

    @Builder.Default
    private Integer taskId = 0;

    @Builder.Default
    private Date taskStartTime = new Date(0);

    /**
     * 获取宠物信息的字符串表示
     *
     * @return 宠物信息字符串
     */
    public String infoMessage() {
        update();
        return (this.isDead ? "宠物已经死亡" :
                "宠物名称: " + this.petName + "\n" +
                "等级: " + this.petLevel + "\n" +
                "经验值: " + this.petExp + "/" + this.petMaxExp + "\n" +
                "生命值: " + df.format(this.petHp) + "/" + this.petMaxHp + "\n" +
                "饥饿值: " + df.format(this.petHunger) + "/" + this.petMaxHunger + "\n" +
                "心情值: " + df.format(this.petMood) + "/" + this.petMaxMood + "\n" +
                "清洁值: " + df.format(this.petHealth) + "/" + this.petMaxHealth + "\n" +
                "能量值: " + df.format(this.petEnergy) + "/" + this.petMaxEnergy + "\n" +
                "每分钟变化值: " + this.petType.getValueChangePerMin() + "\n" +
                (this.isSleeping ? "宠物正在睡觉\n" : "") +
                (this.isSick ? "宠物生病了\n" : ""));
    }

    /**
     * 更新宠物状态
     */
    public void update() {
        Long timeDifference = DateUtil.between(this.lastUpdateTime, new Date(), DateUnit.MINUTE);
        if (timeDifference <= 1 || this.isSleeping || this.isDead) {
            return;
        }

        this.lastUpdateTime = new Date();
        updateHunger(timeDifference);
        updateMood(timeDifference);
        updateClean(timeDifference);
        updateHp(timeDifference);
        levelUp();
        HibernateFactory.merge(this);
    }

    /**
     * 更新宠物饥饿值
     *
     * @param timeDifference 时间差（分钟）
     */
    private void updateHunger(Long timeDifference) {
        if (this.petHunger > 0) {
            this.petHunger -= timeDifference * this.petType.getValueChangePerMin();
            if (this.petHunger < 0) {
                this.petHunger = 0.0;
            }
        } else {
            this.petHp -= timeDifference * this.petType.getValueChangePerMin();
        }
        this.petHunger = Double.parseDouble(df.format(this.petHunger));
        this.petHp = Double.parseDouble(df.format(this.petHp));
    }

    /**
     * 更新宠物心情值
     *
     * @param timeDifference 时间差（分钟）
     */
    private void updateMood(Long timeDifference) {
        double floatValue = RandomUtil.randomDouble(0, 2);
        double needChange = timeDifference * this.petType.getValueChangePerMin() + floatValue;

        if (this.petMood > 0) {
            this.petMood -= needChange;

            if (this.petMood < 0) {
                this.petMood = 0.0;
            }
        } else {
            this.petHp -= needChange;
        }
        this.petMood = Double.parseDouble(df.format(this.petMood));
        this.petHp = Double.parseDouble(df.format(this.petHp));
    }

    /**
     * 更新宠物清洁值
     *
     * @param timeDifference 时间差（分钟）
     */
    private void updateClean(Long timeDifference) {
        double floatValue = RandomUtil.randomDouble(0, 1.5);
        double needChange = timeDifference * this.petType.getValueChangePerMin() + floatValue;

        if (this.petHealth > 0) {
            this.petHealth -= needChange;
            if (this.petHealth < 0) {
                this.petHealth = 0.0;
            }
            this.isSick = this.petHealth < 50;
        } else {
            this.petHp -= needChange;
        }
        this.petHealth = Double.parseDouble(df.format(this.petHealth));
        this.petHp = Double.parseDouble(df.format(this.petHp));
    }

    /**
     * 更新宠物生命值
     *
     * @param timeDifference 时间差（分钟）
     */
    private void updateHp(Long timeDifference) {
        if (this.petHp <= 0) {
            this.isDead = true;
            return;
        }
        if (this.petHp >= this.petMaxHp) {
            this.petHp = Double.parseDouble(df.format(this.petMaxHp));
            return;
        }
        if (this.petHunger > this.petMaxHunger * 0.7 &&
                this.petMood > this.petMaxMood * 0.5 &&
                this.petHealth > this.petMaxHealth * 0.6
        ) {
            this.petHp += this.petType.getValueChangePerMin() * 2 * timeDifference;
        }

        if (this.petHp < 0) {
            this.petHp = 0.0;
        }
        this.petHp = Double.parseDouble(df.format(this.petHp));
    }

    /**
     * 更新宠物睡眠状态
     */
    private void updateSleepStatus() {
        this.isSleeping = !this.isSleeping;
    }

    private void addPetRelationship() {
        this.petRelationship += 0.01;
        this.petRelationship = Double.parseDouble(df.format(this.petRelationship));
        HibernateFactory.merge(this);
    }

    /**
     * 更新宠物能量值
     */
    private void updateEnergy() {
        // TODO 没想好能量值干什么
    }

    public void save() {
        HibernateFactory.merge(this);
    }

    /**
     * 增加宠物的经验值
     *
     * @param amount 增加的经验值数量
     */
    public void addExp(long amount) {
        while (amount > 0) {
            long expNeeded = this.petMaxExp - this.petExp;
            if (amount >= expNeeded) {
                this.petExp = this.petMaxExp;
                amount -= expNeeded;
                levelUp();
            } else {
                this.petExp += amount;
                amount = 0;
            }
        }
    }

    /**
     * 增加宠物的心情值
     *
     * @param amount 增加的心情值数量
     */
    public void addMood(double amount) {
        this.petMood += amount;
        if (this.petMood > this.petMaxMood) {
            this.petMood = Double.parseDouble(df.format(this.petMaxMood));
        }
    }

    /**
     * 增加宠物的饥饿值
     *
     * @param amount 增加的饥饿值数量
     */
    public void addHunger(double amount) {
        this.petHunger += amount;
        if (this.petHunger > this.petMaxHunger) {
            this.petHunger = Double.parseDouble(df.format(this.petMaxHunger));
        }
    }

    /**
     * 增加宠物的清洁值
     *
     * @param amount 增加的清洁值数量
     */
    public void addHealth(double amount) {
        this.petHealth += amount;
        if (this.petHealth > this.petMaxHealth) {
            this.petHealth = Double.parseDouble(df.format(this.petMaxHealth));
        } else if (this.petHealth < 0) {
            this.petHealth = 0.0;
        }
    }

    /**
     * 增加宠物的好感度
     *
     * @param amount 增加的好感度数量
     */
    public void addRelationship(double amount) {
        this.petRelationship += amount;
        this.petRelationship = Double.parseDouble(df.format(this.petRelationship));
    }

    private void levelUp() {

        if (this.petExp >= this.petMaxExp) {
            this.petExp = 0L;
            this.petLevel++;
            this.petMaxExp = (long) (this.petMaxExp * Math.max(1.01, 1.4 - 0.0039 * Math.min(this.petLevel, 100)));
            this.petMaxHp = (int) (this.petMaxHp * 1.1);
            this.petMaxEnergy = (int) (this.petMaxEnergy * 1.1);
            this.petMaxHunger = (int) (this.petMaxHunger * 1.1);
            this.petMaxMood = (int) (this.petMaxMood * 1.2);
            this.petMaxHealth = (int) (this.petMaxHealth * 1.1);
            this.petRelationship += 0.5 * this.petLevel;
        }
    }

    private void reborn() {
        this.isDead = false;
        this.petHp = 10.0;
        this.petHunger = Double.valueOf(this.petMaxHunger);
        this.petMood = Double.valueOf(this.petMaxMood);
        this.petHealth = Double.valueOf(this.petMaxHealth);
        this.petEnergy = Double.valueOf(this.petMaxEnergy);
    }
}