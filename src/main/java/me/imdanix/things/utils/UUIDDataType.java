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