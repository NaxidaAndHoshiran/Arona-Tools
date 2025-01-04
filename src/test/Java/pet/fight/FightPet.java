package pet.fight;

import cn.travellerr.aronaTools.electronicPets.fight.type.AttributeType;
import cn.travellerr.aronaTools.entity.PetInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * 代表一个战斗宠物的类
 */
@Getter
@Setter
public class FightPet {
    /**
     * 宠物信息对象
     */
    private PetInfo petInfo;

    private AttributeType attributeType;

    /**
     * 宠物的生命值
     */
    private double Hp;

    /**
     * 宠物的攻击力
     */
    private double attack;

    /**
     * 宠物的防御力
     */
    private double defend;

    /**
     * 宠物的速度
     */
    private double speed;

    /**
     *宠物所持技能费
     */
    private int cost;

    /**
     * 构造函数，初始化战斗宠物的属性
     *
     * @param petInfo 宠物信息对象
     */
    public FightPet(PetInfo petInfo) {
        this.petInfo = petInfo;
        this.attributeType = petInfo.getPetType().getAttributeType();
        this.Hp = petInfo.getPetHp();
        this.attack = petInfo.getPetEnergy();
        this.defend = petInfo.getPetHealth();
        this.speed = petInfo.getPetLevel() * petInfo.getValueChangePerMin() * 10;
        this.cost = 3;
    }

    public void addHp(double hp) {
        this.Hp += hp;
        if (this.Hp > petInfo.getPetHp()) {
            this.Hp = petInfo.getPetHp();
        }
        if (this.Hp < 0) {
            this.Hp = 0;
        }
    }

    public void addAttack(double attack) {
        this.attack += attack;
        if (this.attack < 0) {
            this.attack = 0;
        }
    }

    public void addDefend(double defend) {
        this.defend += defend;
        if (this.defend < 0) {
            this.defend = 0;
        }
    }

    public void addCost() {
        this.cost += 1;
    }

    public void addCost(int cost) {
        this.cost += cost;
    }
}