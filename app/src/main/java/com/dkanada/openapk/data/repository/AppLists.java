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

    public static final Serializer<AppLists> SERIALIZER = new AppListsSerializer();
    private static class AppListsSerializer extends Serializer<AppLists> {

        @Override
        public void serialize(SerializationContext context, SerializerOutput output, AppLists object) throws IOException {

            Serial serial = new ByteBufferSerial();
            byte[] serializedData = serial.toByteArray(object.appInstalledList, CollectionSerializers.getListSerializer(AppItem.SERIALIZER));
            output.writeByteArray(serializedData);
            serializedData = serial.toByteArray(object.appSystemList, CollectionSerializers.getListSerializer(AppItem.SERIALIZER));
            output.writeByteArray(serializedData);
            serializedData = serial.toByteArray(object.appDisabledList, CollectionSerializers.getListSerializer(AppItem.SERIALIZER));
            output.writeByteArray(serializedData);
            serializedData = serial.toByteArray(object.appHiddenList, CollectionSerializers.getListSerializer(AppItem.SERIALIZER));
            output.writeByteArray(serializedData);
            serializedData = serial.toByteArray(object.appFavoriteList, CollectionSerializers.getListSerializer(AppItem.SERIALIZER));
            output.writeByteArray(serializedData);
        }

        @Override
        public AppLists deserialize(SerializationContext context, SerializerInput input) throws IOException, ClassNotFoundException {

            Serial serial = new ByteBufferSerial();
            AppLists object = new AppLists();

            object.appInstalledList = serial.fromByteArray(input.readByteArray(), CollectionSerializers.getListSerializer(AppItem.SERIALIZER));
            object.appSystemList = serial.fromByteArray(input.readByteArray(), CollectionSerializers.getListSerializer(AppItem.SERIALIZER));
            object.appDisabledList = serial.fromByteArray(input.readByteArray(), CollectionSerializers.getListSerializer(AppItem.SERIALIZER));
            object.appHiddenList = serial.fromByteArray(input.readByteArray(), CollectionSerializers.getListSerializer(AppItem.SERIALIZER));
            object.appFavoriteList = serial.fromByteArray(input.readByteArray(), CollectionSerializers.getListSerializer(AppItem.SERIALIZER));
            return object;
        }
    }
}
