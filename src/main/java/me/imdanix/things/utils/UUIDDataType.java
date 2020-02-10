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

package me.imdanix.things.utils;

import java.util.UUID;
import java.nio.ByteBuffer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataAdapterContext;

public class UUIDDataType implements PersistentDataType<byte[], UUID> {
	public static final UUIDDataType UUID = new UUIDDataType();

	@Override
	public Class<byte[]> getPrimitiveType() {
		return byte[].class;
	}

	@Override
	public Class<UUID> getComplexType() {
		return UUID.class;
	}

	@Override
	public byte[] toPrimitive(UUID complex, PersistentDataAdapterContext context) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(complex.getMostSignificantBits());
		bb.putLong(complex.getLeastSignificantBits());
		return bb.array();
	}

	@Override
	public UUID fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
		ByteBuffer bb = ByteBuffer.wrap(primitive);
		long firstLong = bb.getLong();
		long secondLong = bb.getLong();
		return new UUID(firstLong, secondLong);
	}
}