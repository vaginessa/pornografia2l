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
package instances.ChamberOfProphecies;

import com.l2jmobius.commons.util.CommonUtil;
import com.l2jmobius.gameserver.ai.CtrlIntention;
import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Attackable;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.instancezone.Instance;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmobius.gameserver.network.serverpackets.ExShowUsm;
import com.l2jmobius.gameserver.network.serverpackets.NpcSay;
import com.l2jmobius.gameserver.network.serverpackets.PlaySound;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;

import instances.AbstractInstance;
import quests.Q10753_WindsOfFateChoices.Q10753_WindsOfFateChoices;

/**
 * Chamber of Propercies instance.
 * @author Gigi
 */
public final class ChamberOfProphecies extends AbstractInstance
{
	// NPCs
	private static final int KAIN_VAN_HALTER = 33979;
	private static final int VAN_HALTER = 33999;
	private static final int FERIN = 34001;
	private static final int GRAIL = 33996;
	private static final int MYSTERIOUS_WIZARD = 33980;
	// Misc
	private static final int TEMPLATE_ID = 255;
	private static final int PROPHECY_MACHINE = 39540;
	private static final int ATELIA = 39542;
	private static final Location FIRST_ROOM_LOC = new Location(-88503, 184754, -10440, 48891);
	// Monsters
	private static final int[] ATTACABLE_MONSTERS =
	{
		19568, // Sacred Wizard
		19569, // Sacred Soldier
		19570, // Sacred Slayer
		19571, // Makkum
		19572, // Abyssal Shadow
		19573, // Secluded Shadow
	};
	
	public ChamberOfProphecies()
	{
		super(TEMPLATE_ID);
		addStartNpc(KAIN_VAN_HALTER);
		addFirstTalkId(KAIN_VAN_HALTER, GRAIL, MYSTERIOUS_WIZARD);
		addTalkId(KAIN_VAN_HALTER, GRAIL, MYSTERIOUS_WIZARD);
		addSeeCreatureId(FERIN);
		addSeeCreatureId(VAN_HALTER);
		addKillId(ATTACABLE_MONSTERS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = player.getQuestState(Q10753_WindsOfFateChoices.class.getSimpleName());
		if (event.equals("enterInstance"))
		{
			enterInstance(player, npc, TEMPLATE_ID);
			if (hasQuestItems(player, PROPHECY_MACHINE))
			{
				takeItems(player, PROPHECY_MACHINE, 1);
			}
			qs.setCond(16, true);
		}
		else
		{
			final Instance world = npc.getInstanceWorld();
			if (isInInstance(world))
			{
				switch (event)
				{
					case "33979-01.html":
					case "33996-01.html":
					case "33980-01.html":
					case "33980-02.html":
					{
						htmltext = event;
						break;
					}
					case "33996-02.html":
					{
						world.broadcastPacket(ExShowUsm.USM_Q015_E); // TODO not show usim movie
						world.despawnGroup("q10753_16_instance_grail");
						world.spawnGroup("q10753_16_instance_wizard");
						giveItems(player, ATELIA, 1);
						showOnScreenMsg(player, NpcStringId.TALK_TO_THE_MYSTERIOUS_WIZARD, ExShowScreenMessage.TOP_CENTER, 6000);
						htmltext = event;
						break;
					}
					case "33980-03.html":
					{
						showOnScreenMsg(player, NpcStringId.THIS_CHOICE_CANNOT_BE_REVERSED, ExShowScreenMessage.TOP_CENTER, 6000);
						htmltext = event;
						break;
					}
					case "33980-04.html":
					{
						showOnScreenMsg(player, NpcStringId.THIS_CHOICE_CANNOT_BE_REVERSED, ExShowScreenMessage.TOP_CENTER, 6000);
						htmltext = event;
						break;
					}
					case "33980-05.html":
					{
						world.spawnGroup("q10753_16_instance_halter_2");
						world.setStatus(6);
						startQuestTimer("DESPAWN_WIZARD", 2000, npc, player);
						htmltext = event;
						break;
					}
					case "status":
					{
						if (world.isStatus(0))
						{
							htmltext = "33979-01.html";
							break;
						}
						htmltext = "33979-02.html";
						break;
					}
					case "teleport":
					{
						player.teleToLocation(FIRST_ROOM_LOC);
						world.spawnGroup("q10753_16_instance_halter_1_1");
						world.spawnGroup("wof_room1");
						break;
					}
					case "ATTACK":
					case "ATTACK1":
					case "ATTACK2":
					{
						npc.setIsRunning(true);
						((L2Attackable) npc).setCanReturnToSpawnPoint(false);
						((L2Attackable) npc).setCanStopAttackByTime(false);
						if (npc.isScriptValue(0) && world.getAliveNpcs(ATTACABLE_MONSTERS).isEmpty())
						{
							npc.setTarget(player);
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
						}
						else
						{
							if (npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
							{
								L2World.getInstance().forEachVisibleObjectInRange(npc, L2Npc.class, 3000, chars ->
								{
									if (CommonUtil.contains(ATTACABLE_MONSTERS, chars.getId()))
									{
										addAttackDesire(npc, chars);
										return;
									}
								});
							}
						}
						break;
					}
					case "OPEN_DOOR1":
					{
						cancelQuestTimers("ATTACK");
						world.setStatus(1);
						world.openCloseDoor(17230102, true);
						world.spawnGroup("wof_room2");
						break;
					}
					case "OPEN_DOOR2":
					{
						cancelQuestTimers("ATTACK1");
						final int halter = world.getTemplateParameters().getInt("vanhalter");
						final L2Npc npcss = world.getNpc(halter);
						startQuestTimer("ATTACK2", 200, npcss, player, true);
						world.setStatus(3);
						world.spawnGroup("wof_room3");
						world.openCloseDoor(17230103, true);
						break;
					}
					case "BROADCAST_TEXT":
					{
						npc.setTarget(player);
						npc.setRunning();
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.THAT_GUY_KAIN_HAS_A_SMARMY_FACE));
						player.sendPacket(new PlaySound(3, "Npcdialog1.apple_quest_7", 0, 0, 0, 0, 0));
						break;
					}
					case "SEY2":
					{
						if (npc.getId() == FERIN)
						{
							npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.YOU_CAN_T_DIE_HERE_I_DIDN_T_LEARN_RESURRECT_YET));
							player.sendPacket(new PlaySound(3, "Npcdialog1.apple_quest_4", 0, 0, 0, 0, 0));
						}
						break;
					}
					case "SEY_KAIN":
					{
						if (npc.getId() == VAN_HALTER)
						{
							startQuestTimer("ATTACK1", 200, npc, player, true);
							npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.GISELLE_WAS_SUCH_A_SWEET_CHILD));
							player.sendPacket(new PlaySound(3, "Npcdialog1.holter_quest_1", 0, 0, 0, 0, 0));
						}
						break;
					}
					case "SEY3":
					{
						if (npc.getId() == FERIN)
						{
							npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.DO_YOU_THINK_I_LL_GROW_TALLER_IF_I_EAT_LOTS_AND_LOTS));
							player.sendPacket(new PlaySound(3, "Npcdialog1.apple_quest_6", 0, 0, 0, 0, 0));
						}
						break;
					}
					case "SEY_KAIN_1":
					{
						if (npc.getId() == VAN_HALTER)
						{
							npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.SUCH_MONSTERS_IN_A_PLACE_LIKE_THIS_UNBELIEVABLE));
						}
						break;
					}
					case "SEY_KAIN_2":
					{
						if (npc.getId() == VAN_HALTER)
						{
							npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.THAT_S_THE_MONSTER_THAT_ATTACKED_FAERON_YOU_RE_OUTMATCHED_HERE_GO_AHEAD_I_LL_CATCH_UP));
							player.sendPacket(new PlaySound(3, "Npcdialog1.holter_quest_6", 0, 0, 0, 0, 0));
							startQuestTimer("SEY_KAIN_3", 7000, npc, player);
						}
						break;
					}
					case "SEY4":
					{
						if (npc.getId() == FERIN)
						{
							npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.GO_NOW_KAIN_CAN_HANDLE_THIS));
							startQuestTimer("REST", 5000, npc, player);
							npc.setScriptValue(1);
						}
						break;
					}
					case "SEY_KAIN_3":
					{
						if (npc.getId() == VAN_HALTER)
						{
							npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.LEAVE_THIS_TO_ME_GO));
							startQuestTimer("SEY_KAIN_4", 1000, npc, player);
							npc.setScriptValue(1);
						}
						break;
					}
					case "REST":
					{
						if (npc.getId() == FERIN)
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, player);
							cancelQuestTimers("BROADCAST_TEXT");
						}
						break;
					}
					case "SEY_KAIN_4":
					{
						world.setStatus(5);
						world.spawnGroup("q10753_16_instance_grail");
						showOnScreenMsg(player, NpcStringId.LEAVE_THIS_PLACE_TO_KAIN_NGO_TO_THE_NEXT_ROOM, ExShowScreenMessage.TOP_CENTER, 6000);
						world.openCloseDoor(17230104, true);
						cancelQuestTimers("ATTACK2");
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, player);
						startQuestTimer("CLOSE", 15000, npc, player);
						break;
					}
					case "CLOSE":
					{
						world.openCloseDoor(17230104, false);
						world.despawnGroup("q10753_16_instance_halter_1_1");
						world.despawnGroup("wof_room4");
						break;
					}
					case "DESPAWN_WIZARD":
					{
						world.despawnGroup("q10753_16_instance_wizard");
						break;
					}
					case "exit":
					{
						startQuestTimer("finish", 3000, npc, player);
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTE_S_YOU_WILL_BE_FORCED_OUT_OF_THE_DUNGEON_WHEN_THE_TIME_EXPIRES).addInt((int) 1.0D));
						qs.setCond(17, true);
						break;
					}
					case "finish":
					{
						world.finishInstance(0);
						break;
					}
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			switch (world.getStatus())
			{
				case 0:
				{
					if (world.getAliveNpcs(ATTACABLE_MONSTERS).isEmpty())
					{
						final int ferin = world.getTemplateParameters().getInt("ferin");
						final int halter = world.getTemplateParameters().getInt("vanhalter");
						final L2Npc npcs = world.getNpc(ferin);
						final L2Npc npcss = world.getNpc(halter);
						startQuestTimer("SEY2", 14000, npcs, player);
						startQuestTimer("SEY_KAIN", 24000, npcss, player);
						startQuestTimer("OPEN_DOOR1", 5000, npc, player);
					}
					break;
				}
				case 1:
				{
					if (world.getAliveNpcs(ATTACABLE_MONSTERS).isEmpty())
					{
						world.spawnGroup("wof_room2_1");
						world.setStatus(2);
					}
					break;
				}
				case 2:
				{
					if (world.getAliveNpcs(ATTACABLE_MONSTERS).isEmpty())
					{
						final int ferin = world.getTemplateParameters().getInt("ferin");
						final L2Npc npcs = world.getNpc(ferin);
						startQuestTimer("SEY3", 8000, npcs, player);
						startQuestTimer("OPEN_DOOR2", 5000, npc, player);
					}
					break;
				}
				case 3:
				{
					if (world.getAliveNpcs(ATTACABLE_MONSTERS).isEmpty())
					{
						world.setStatus(4);
						world.spawnGroup("wof_room3_2");
						world.openCloseDoor(17230103, false);
						final int halter = world.getTemplateParameters().getInt("vanhalter");
						final L2Npc npcss = world.getNpc(halter);
						startQuestTimer("SEY_KAIN_1", 5000, npcss, player);
					}
					break;
				}
				case 4:
				{
					if (world.getAliveNpcs(ATTACABLE_MONSTERS).isEmpty())
					{
						world.setStatus(5);
						world.spawnGroup("wof_room4"); // TODO spawn animation
						final int halter = world.getTemplateParameters().getInt("vanhalter");
						final int ferin = world.getTemplateParameters().getInt("ferin");
						final L2Npc npcss = world.getNpc(halter);
						final L2Npc npcs = world.getNpc(ferin);
						startQuestTimer("SEY_KAIN_2", 3000, npcss, player);
						startQuestTimer("SEY4", 7000, npcs, player);
					}
					break;
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = player.getQuestState(Q10753_WindsOfFateChoices.class.getSimpleName());
		String htmltext = null;
		switch (npc.getId())
		{
			case KAIN_VAN_HALTER:
			{
				if ((qs != null) && qs.isCond(16))
				{
					htmltext = "33979.html";
				}
				break;
			}
			case GRAIL:
			{
				htmltext = "33996.html";
				break;
			}
			case MYSTERIOUS_WIZARD:
			{
				if ((qs != null) && qs.isCond(16))
				{
					htmltext = "33980.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			switch (npc.getId())
			{
				case FERIN:
				{
					if (creature.isPlayer() && !creature.isDead() && npc.isScriptValue(0))
					{
						startQuestTimer("BROADCAST_TEXT", 12000, npc, (L2PcInstance) creature);
					}
					break;
				}
				case VAN_HALTER:
				{
					if (creature.isPlayer() && !creature.isDead() && world.isStatus(0))
					{
						startQuestTimer("ATTACK", 2000, npc, (L2PcInstance) creature, true);
					}
					break;
				}
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	public static void main(String[] args)
	{
		new ChamberOfProphecies();
	}
}