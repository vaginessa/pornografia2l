/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.stats.Stats;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.EtcStatusUpdate;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * Focus Max Energy effect implementation.
 * @author Adry_85
 */
public final class FocusMaxMomentum extends AbstractEffect
{
	public FocusMaxMomentum(StatsSet params)
	{
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		if (effected.isPlayer())
		{
			final L2PcInstance player = effected.getActingPlayer();
			
			final int count = (int) effected.getStat().getValue(Stats.MAX_MOMENTUM, 0);
			
			player.setCharges(count);
			
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_FORCE_HAS_INCREASED_TO_LEVEL_S1);
			sm.addInt(count);
			player.sendPacket(sm);
			
			player.sendPacket(new EtcStatusUpdate(player));
		}
	}
}