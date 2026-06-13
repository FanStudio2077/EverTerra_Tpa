package com.everterra.tpa.core;

import java.util.UUID;

/**
 * Represents a teleport request between two players.
 */
public class TpaRequest {

    private final UUID sender;
    private final UUID target;
    private final TpaType type;
    private final long createTime;
    private final long expireTime;

    public TpaRequest(UUID sender, UUID target, TpaType type, long expireTimeSeconds) {
        this.sender = sender;
        this.target = target;
        this.type = type;
        this.createTime = System.currentTimeMillis();
        this.expireTime = this.createTime + (expireTimeSeconds * 1000L);
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getTarget() {
        return target;
    }

    public TpaType getType() {
        return type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    /**
     * Checks if this request has expired.
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }

    /**
     * Gets remaining seconds before expiry.
     */
    public long getRemainingSeconds() {
        long remaining = (expireTime - System.currentTimeMillis()) / 1000L;
        return Math.max(0, remaining);
    }

    /**
     * Gets a human-readable type name.
     */
    public String getTypeDisplay() {
        return type == TpaType.TPA ? "TPA" : "TPAC";
    }
}
