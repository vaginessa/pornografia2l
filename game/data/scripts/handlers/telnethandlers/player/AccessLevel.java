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
package handlers.telnethandlers.player;

import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.telnet.ITelnetCommand;
import com.l2jmobius.gameserver.network.telnet.TelnetServer;
import com.l2jmobius.gameserver.util.Util;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class AccessLevel implements ITelnetCommand
{
	private AccessLevel()
	{
	}
	
	@Override
	public String getCommand()
	{
		return "accesslevel";
	}
	
	@Override
	public String getUsage()
	{
		return "AccessLevel <player name> <access level>";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length < 2) || args[0].isEmpty() || !Util.isDigit(args[1]))
		{
			return null;
		}
		final L2PcInstance player = L2World.getInstance().getPlayer(args[0]);
		if (player != null)
		{
			final int level = Integer.parseInt(args[1]);
			player.setAccessLevel(level, true, true);
			return "Player " + player.getName() + "'s access level has been changed to: " + level;
		}
		return "Couldn't find player with such name.";
	}
	
	public static void main(String[] args)
	{
		TelnetServer.getInstance().addHandler(new AccessLevel());
	}
}
