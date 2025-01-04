package pet.fight;

import cn.travellerr.aronaTools.electronicPets.use.type.PetType;
import cn.travellerr.aronaTools.entity.PetInfo;

public class Tester {
    public static void main(String[] args) {
        // 创建两个宠物信息对象
        PetInfo petInfo1 = PetInfo.builder().petName("测试1").petType(PetType.CAT).valueChangePerMin(PetType.CAT.getValueChangePerMin()).build();
        PetInfo petInfo2 = PetInfo.builder().petName("测试2").petType(PetType.DOG).valueChangePerMin(PetType.DOG.getValueChangePerMin()).build();

        // 创建战斗场地对象
        BattleGround battleGround = new BattleGround(new FightPet(petInfo1), new FightPet(petInfo2));

        // 开始战斗
        System.out.println(battleGround.startBattle());

        // 模拟20回合的战斗
        for (int i = 0; i < 20; i++) {
            // 切换到下一个宠物
            String nextOne = battleGround.nextOne();
            if (nextOne.contains("-1")) {
                break;
            }
            System.out.println("第" + battleGround.nextRound() + "回合\n");

            System.out.println(nextOne);

            // 自动攻击
            System.out.println(battleGround.autoAttack());
/*            System.out.println(battleGround.selectSkill());
            System.out.println(battleGround.confrontation("我玩原神") + "\n");*/

            // 切换到下一个宠物
            nextOne = battleGround.nextOne();
            if (nextOne.contains("-1")) {
                break;
            } else {
                System.out.println(nextOne);
            }

            // 自动攻击
            System.out.println(battleGround.autoAttack());
/*            System.out.println(battleGround.selectSkill());
            System.out.println(battleGround.confrontation("1") + "\n");*/
        }

        // 结束战斗并输出结果
        System.out.println(battleGround.endBattle(null));
    }
}