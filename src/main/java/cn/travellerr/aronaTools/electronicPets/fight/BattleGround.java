package cn.travellerr.aronaTools.electronicPets.fight;

import cn.hutool.core.util.RandomUtil;
import cn.travellerr.aronaTools.electronicPets.fight.factory.Battle;
import cn.travellerr.aronaTools.electronicPets.fight.type.AttributeType;
import cn.travellerr.aronaTools.entity.PetInfo;
import cn.travellerr.aronaTools.shareTools.Log;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


/**
 * BattleGround类实现了Battle接口，表示一个战斗场地。
 * 该类包含了战斗的主要逻辑，包括开始战斗、自动攻击、技能选择等。
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class BattleGround implements Battle {
    /**
     * 当前宠物
     */
    @NotNull private FightPet nowPet;

    /**
     * 下一个宠物
     */
    @NotNull private FightPet nextPet;

    /**
     * 上一个使用的技能
     */
    private Skill previousSkill;

    /**
     * 战斗进度
     */
    private int progress;

    /**
     * 当前回合数
     */
    private int round;

    /**
     * 构造函数，初始化战斗场地。
     * @param petInfo1 第一个宠物信息
     * @param petInfo2 第二个宠物信息
     */
    public BattleGround(@NotNull FightPet petInfo1, @NotNull FightPet petInfo2) {
        this.nowPet = petInfo1;
        this.nextPet = petInfo2;
    }

    /**
     * 开始战斗。
     * @return 返回战斗开始的信息
     */
    @Override
    public String startBattle() {
        if (progress == 0) {
            progress = 1;
            return """
                    战斗开始！
                    %user1% VS %user2%
                    
                    %@user1% 你的宠物
                    """ + nowPet.getPetInfo().getPetName() + """
                    
                    %@user2% 你的宠物
                    """ + nextPet.getPetInfo().getPetName();
        } else {
            return "战斗已经开始！";
        }
    }

    /**
     * 自动攻击逻辑。
     * @return 返回自动攻击的结果
     */
    @Override
    public String autoAttack() {
        // 获取可用技能列表
        // 使用`Arrays.stream(Skill.values())`将所有技能转换为流。
        List<Skill> skillList = Arrays.stream(Skill.values())
            // 过滤出属性类型不为空且与当前宠物属性类型匹配的技能，或技能适用于当前宠物类型。
            .filter(skill -> (skill.getAttributeType() != null &&
                    (skill.getAttributeType() == AttributeType.ALL ||
                            skill.getAttributeType().equals(this.nowPet.getPetInfo().getPetType().getAttributeType())) ||
                            Arrays.stream(skill.getPetType()).anyMatch(petType -> petType.equals(this.nowPet.getPetInfo().getPetType()))))
            // 进一步过滤出技能消耗不超过当前宠物可用技能点的技能。
            .filter(skill -> skill.getDefaultCost() <= this.nowPet.getCost())
            // 按技能性价比排序（默认值/默认消耗）
            .sorted((s1, s2) -> Double.compare((double) s2.getDefaultValue() / s2.getDefaultCost(), (double) s1.getDefaultValue() / s1.getDefaultCost()))
            // 将过滤后的技能流转换为列表。
            .toList();

        // 根据当前宠物的生命值和上一个技能类型选择技能
        if (nowPet.getHp() < nowPet.getPetInfo().getPetHp() * 0.3) {
            skillList = skillList.stream().filter(skill -> skill.getSkillType().equals(SkillType.HEALTH)).toList();
        } else if (previousSkill != null && previousSkill.getSkillType() == SkillType.ATTACK && Math.random() < 0.5) {
            skillList = skillList.stream().filter(skill -> skill.getSkillType().equals(SkillType.DEFEND)).toList();
        } else {
            skillList = skillList.stream().filter(skill -> skill.getSkillType().equals(SkillType.ATTACK)).toList();
        }

        Log.debug("选择技能");

        // 随机选择一个技能
        Skill skill = skillList.get((int) (Math.random() * skillList.size()));

        Log.debug("随机选择技能: " + skill.getName());

        // 执行技能并获取伤害值
        int damage = action(skill);

        Log.debug("执行技能");

        previousSkill = skill;

        // 返回攻击结果
        Log.debug("返回攻击结果");
        return "自动攻击：\n" +
                nowPet.getPetInfo().getPetName()
                +" 使用 " + skill.getName() + " 耗费 " + skill.getDefaultCost() + " cost" + (skill.getSkillType().equals(SkillType.ATTACK) ? " 对 "+ this.nextPet.getPetInfo().getPetName()+
                " 造成了 "+damage+" 点伤害。\n" +
                "当前 "+ this.nextPet.getPetInfo().getPetName()+" 的生命值："+ this.nextPet.getHp() :
                (skill.getSkillType().equals(SkillType.DEFEND) ? " 进行了防御。\n" : " 进行了恢复。\n"+
                        "当前 "+nowPet.getPetInfo().getPetName()+" 的生命值："+ this.nowPet.getHp())) +
                "\n当前 %user1% cost："+ this.nowPet.getCost();
    }

    /**
     * 执行技能操作。
     * @param skill 要执行的技能
     * @return 返回技能的实际效果值
     */
    @Override
    public int action(Skill skill) {
        // 获取技能的默认值
        int value = skill.getDefaultValue();
        this.nowPet.addCost(-skill.getDefaultCost()); // 扣除技能费

        this.nowPet.addCost(); // 增加技能费

        // 根据技能类型执行不同的操作
        if (skill.getSkillType().equals(SkillType.ATTACK)) {
            // 判断属性相克关系
            int againstResult = this.nowPet.getAttributeType().isWeakAgainst(this.nextPet.getAttributeType());
            if (againstResult == 1) {
                value = (int) (value * 1.5); // 属性克制，伤害增加50%
            } else if (againstResult == -1) {
                value = (int) (value * 0.5); // 属性被克制，伤害减少50%
            }

            this.nextPet.addDefend(-value); // 扣除对方宠物的防御值
            if (this.nextPet.getDefend() < 0) {
                value = (int) -this.nextPet.getDefend();
                this.nextPet.setDefend(0);
            }
            this.nextPet.addHp(-value); // 扣除对方宠物的生命值
        } else if (skill.getSkillType().equals(SkillType.DEFEND)) {
            nowPet.addDefend(skill.getDefaultValue()); // 增加防御值
        } else if (skill.getSkillType().equals(SkillType.HEALTH)) {
            nowPet.addHp(skill.getDefaultValue()); // 恢复生命值
        } else if (skill.getSkillType().equals(SkillType.CONTROL)) {
            // 控制技能
            // 判断属性相克关系
            int againstResult = this.nowPet.getAttributeType().isWeakAgainst(this.nextPet.getAttributeType());
            if (againstResult == 1) {
                value = (int) (value * 1.5); // 属性克制，伤害增加50%
            } else if (againstResult == -1) {
                value = (int) (value * 0.5); // 属性被克制，伤害减少50%
            }
            this.nextPet.addDefend(-value); // 扣除对方宠物的防御值
            if (this.nextPet.getDefend() < 0) {
                this.nextPet.setDefend(0);
                this.nextPet.setControlled(true); // 控制对方宠物
            }
        }

        return value; // 返回技能的实际效果值
    }

    /**
     * 结束战斗并确定胜利者。
     * @param theSurrender 投降的宠物信息
     * @return 返回战斗结束的信息
     */
    @Override
    public String endBattle(PetInfo theSurrender) {
        // 确定战斗的胜利者
        Log.debug("确定战斗胜利者");
        FightPet winner = (theSurrender == null)? (nowPet.getHp() > nextPet.getHp() ? nowPet : nextPet)
                : (nowPet.getPetInfo().getPetName().equals(theSurrender.getPetName()) ? nextPet : nowPet);

        Log.debug("确定战斗失败者");
        FightPet loser = (theSurrender == null)? (nowPet.getHp() > nextPet.getHp() ? nextPet : nowPet) : (nowPet.getPetInfo().getPetName().equals(theSurrender.getPetName()) ? nowPet : nextPet);

        Log.debug("计算经验值和金币");
        PetInfo petInfoWin = winner.getPetInfo();
        PetInfo petInfoLose = loser.getPetInfo();
        int changeExp = (int) (petInfoLose.getPetExp() * (Math.random() * 0.2 + 0.1));

        Log.debug("增加经验值和金币");
        petInfoWin.addExp(changeExp);
        petInfoLose.addExp(-changeExp);
        int changeMoney = RandomUtil.randomInt(10, 101);
        petInfoWin.addPetCoin(changeMoney);
        petInfoLose.addPetCoin(-changeMoney);

        // 返回战斗结束结果
        return "战斗结束！\n" +
                "获胜者是："+winner.getPetInfo().getPetName()+"\n" +
                "生命值："+winner.getHp()+"\n" +
                "攻击力："+winner.getAttack()+"\n" +
                "防御力："+winner.getDefend()+"\n" +
                "速度："+winner.getSpeed()+"\n" +
                "技能费："+winner.getCost()+"\n" +
                "增加经验值："+changeExp+"\n" +
                "增加科技点："+changeMoney;
    }

    /**
     * 切换到下一个宠物。
     * @return 返回下一个宠物的信息
     */
    @Override
    public String nextOne() {
        if (this.nextPet.getHp() <= 0) {
            return "-1";
        }
        FightPet temp = this.nowPet;
        this.nowPet = this.nextPet;
        this.nextPet = temp;
        return "%@user1% 的回合";
    }

    /**
     * 进入下一回合。
     * @return 返回当前回合数
     */
    public int nextRound() {
        return ++this.round;
    }

    /**
     * 选择技能。
     * @return 返回可选技能列表
     */
    @Override
    public String selectSkill() {
        List<Skill> skillList = Arrays.stream(Skill.values()).filter(skill ->
                skill.getAttributeType() != null &&(
                skill.getAttributeType() == AttributeType.ALL ||
                skill.getAttributeType().equals(this.nowPet.getPetInfo().getPetType().getAttributeType())) ||
                Arrays.stream(skill.getPetType()).anyMatch(petType -> petType.equals(this.nowPet.getPetInfo().getPetType()))
        ).toList();
        List<String> skillNameList = skillList.stream().map(Skill::getSimplifiedInfo).toList();

        StringBuilder sb = new StringBuilder();

        sb.append("请选择技能：\n");

        skillNameList.forEach(s -> sb.append(skillNameList.indexOf(s)+1).append(". ").append(s).append("\n"));

        return sb.toString();
    }

    /**
     * 执行技能对抗。
     * @param skillName 技能名称或编号
     * @return 返回对抗结果
     */
    @Override
    public String confrontation(String skillName) {
        int index;
        Skill skill;
        try {
            try {
                index = Integer.parseInt(skillName);
                skill = Arrays.stream(Skill.values()).filter(s ->
                        (s.getAttributeType() == null ||
                                s.getAttributeType().equals(this.nowPet.getPetInfo().getPetType().getAttributeType()) ||
                                Arrays.stream(s.getPetType()).anyMatch(petType -> petType.equals(this.nowPet.getPetInfo().getPetType())))
                ).toList().get(index - 1);
            } catch (NumberFormatException e) {
                List<Skill> skillList = Arrays.stream(Skill.values()).filter(s ->
                        (s.getAttributeType() == null ||
                                s.getAttributeType().equals(this.nowPet.getPetInfo().getPetType().getAttributeType()) ||
                                Arrays.stream(s.getPetType()).anyMatch(petType -> petType.equals(this.nowPet.getPetInfo().getPetType())))
                ).toList();
                skill = skillList.stream().filter(s -> s.getName().equals(skillName)).findFirst().orElseThrow(() -> new IllegalArgumentException("Skill not found"));
            }
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            // 处理异常
            return "技能选择错误！"+ e.getMessage();
        }

        // 执行技能并获取伤害值
        int damage = action(skill);

        previousSkill = skill;

        // 返回对抗结果
        return nowPet.getPetInfo().getPetName()
                +" 使用 " + skill.getName() + (skill.getSkillType().equals(SkillType.ATTACK) ? " 对 "+ this.nextPet.getPetInfo().getPetName()+
                " 造成了 "+damage+" 点伤害。\n" +
                "当前 "+ this.nextPet.getPetInfo().getPetName()+" 的生命值："+ this.nextPet.getHp()+"\n" :
                (skill.getSkillType().equals(SkillType.DEFEND) ? " 进行了防御。\n" : " 进行了恢复。\n"+
                        "当前 "+nowPet.getPetInfo().getPetName()+" 的生命值："+ this.nowPet.getHp()));
    }
}