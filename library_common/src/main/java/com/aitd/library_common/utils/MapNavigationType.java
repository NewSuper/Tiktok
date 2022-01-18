package com.aitd.library_common.utils;

public class MapNavigationType {

    private int mapId;
    private String mapName;
    private String packageName;

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    MapNavigationType(int mapId,String mapName,String packageName){
        this.mapId = mapId;
        this.mapName = mapName;
        this.packageName = packageName;
    }

}
