package me.imdanix.things.command;

import static me.imdanix.things.utils.Utils.clr;

public enum FailInfo {
	NO_CONSOLE("&cОшибка>&f Эта команда не может быть использована из консоли."),
	NO_PERMISSION("&cОшибка>&f У вас недостаточно прав для использования этой команды."),
	WRONG_ARG("&cОшибка>&f Введен один или более неправильный агрумент - проверьте написание команды."),
	OFFLINE_PLAYER("&cОшибка>&f Данного игрока нет на сервере.");
	private final String info;
	FailInfo(String info) {
		this.info=clr(info);
	}
	@Override
	public String toString() {
		return info;
	}
}
