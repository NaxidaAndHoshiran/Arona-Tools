package pet.fight.factory;

import cn.travellerr.aronaTools.electronicPets.fight.Skill;
import cn.travellerr.aronaTools.entity.PetInfo;

import javax.annotation.Nullable;

public interface Battle {

    String startBattle();
    String confrontation (String skillName);
    String autoAttack();
    int action(Skill skill);
    String endBattle(@Nullable PetInfo theSurrender);
    String nextOne();
    String selectSkill();

}
