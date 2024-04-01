package com.dooji.dpf;

import java.util.Date;
import net.minecraft.SaveVersion;
import net.minecraft.GameVersion;
import net.minecraft.resource.ResourceType;

public class FakeGameVersion implements GameVersion {
    private final String id;
    private final String name;
    private final int protocolVersion;
    private final SaveVersion saveVersion;

    public FakeGameVersion(String id, String name, int protocolVersion) {
        this.id = id;
        this.name = name;
        this.protocolVersion = protocolVersion;
        this.saveVersion = new SaveVersion(2714);
    }

    @Override
    public SaveVersion getSaveVersion() {
        return saveVersion;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public int getResourceVersion(ResourceType var1) {
        return 0;
    }

    @Override
    public Date getBuildTime() {
        return null;
    }

    @Override
    public boolean isStable() {
        return false;
    }
}
