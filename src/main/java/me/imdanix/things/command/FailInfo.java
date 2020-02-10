/*
 * Copyright (C) 2020 imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.things.command;

import static me.imdanix.things.utils.Utils.clr;

public enum FailInfo {
	NO_CONSOLE("&c&lОшибка>&f Эта команда не может быть использована из консоли."),
	NO_PERMISSION("&c&lОшибка>&f У вас недостаточно прав для использования этой команды."),
	WRONG_ARG("&c&lОшибка>&f Введен один или более неправильный агрумент - проверьте написание команды."),
	OFFLINE_PLAYER("&c&lОшибка>&f Данного игрока нет на сервере.");
	private final String info;
	FailInfo(String info) {
		this.info=clr(info);
	}
	@Override
	public String toString() {
		return info;
	}
}
