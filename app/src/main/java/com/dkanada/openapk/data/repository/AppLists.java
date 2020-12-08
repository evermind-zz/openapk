package com.dkanada.openapk.data.repository;

import com.dkanada.openapk.models.AppItem;
import com.twitter.serial.serializer.CollectionSerializers;
import com.twitter.serial.serializer.SerializationContext;
import com.twitter.serial.serializer.Serializer;
import com.twitter.serial.stream.Serial;
import com.twitter.serial.stream.SerializerInput;
import com.twitter.serial.stream.SerializerOutput;
import com.twitter.serial.stream.bytebuffer.ByteBufferSerial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppLists {
    public List<AppItem> appInstalledList = new ArrayList<>();
    public List<AppItem> appSystemList = new ArrayList<>();
    public List<AppItem> appDisabledList = new ArrayList<>();
    public List<AppItem> appHiddenList = new ArrayList<>();
    public List<AppItem> appFavoriteList = new ArrayList<>();
}
