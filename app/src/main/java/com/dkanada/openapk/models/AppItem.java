package com.dkanada.openapk.models;

import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.dkanada.openapk.App;
import com.dkanada.openapk.utils.OtherUtils;
import com.twitter.serial.serializer.SerializationContext;
import com.twitter.serial.serializer.Serializer;
import com.twitter.serial.stream.SerializerInput;
import com.twitter.serial.stream.SerializerOutput;

import java.io.IOException;

public class AppItem implements Parcelable {
    public static final Serializer<AppItem> SERIALIZER = new AppItemSerializer();
    private Bitmap icon;
    private String packageLabel;
    private String packageName;
    private String versionName;
    private String versionCode;
    private String data;
    private String source;
    private String install;
    private String update;
    public boolean system;
    public boolean disable;
    public boolean hide;
    public boolean favorite;

    public AppItem(PackageInfo packageInfo) {
        icon = OtherUtils.drawableToBitmap(App.getPackageIcon(packageInfo));
        packageLabel = App.getPackageName(packageInfo);
        packageName = packageInfo.packageName;
        versionName = packageInfo.versionName;
        versionCode = Integer.toString(packageInfo.versionCode);
        data = packageInfo.applicationInfo.dataDir;
        source = packageInfo.applicationInfo.sourceDir;
        install = Long.toString(packageInfo.firstInstallTime);
        update = Long.toString(packageInfo.lastUpdateTime);
    }

    public AppItem(Parcel parcel) {
        icon = parcel.readParcelable(getClass().getClassLoader());
        packageLabel = parcel.readString();
        packageName = parcel.readString();
        versionName = parcel.readString();
        versionCode = parcel.readString();
        data = parcel.readString();
        source = parcel.readString();
        install = parcel.readString();
        update = parcel.readString();
        boolean[] flags = new boolean[4];
        parcel.readBooleanArray(flags);
        system = flags[0];
        disable = flags[1];
        hide = flags[2];
        favorite = flags[3];
    }

    public AppItem( Bitmap icon,
                    String packageLabel,
                    String packageName,
                    String versionName,
                    String versionCode,
                    String data,
                    String source,
                    String install,
                    String update,
                    boolean system,
                    boolean disable,
                    boolean hide,
                    boolean favorite
    ) {
        this.icon = icon;
        this.packageLabel = packageLabel;
        this.packageName = packageName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.data = data;
        this.source = source;
        this.install = install;
        this.update = update;
        this.system = system;
        this.disable = disable;
        this.hide = hide;
        this.favorite = favorite;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public String getPackageLabel() {
        return packageLabel;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getData() {
        return data;
    }

    public String getSource() {
        return source;
    }

    public String getInstall() {
        return install;
    }

    public String getUpdate() {
        return update;
    }

    public static final Parcelable.Creator<AppItem> CREATOR = new Parcelable.Creator<AppItem>() {
        public AppItem createFromParcel(Parcel parcel) {
            return new AppItem(parcel);
        }
        public AppItem[] newArray(int size) {
            return new AppItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(icon, flags);
        parcel.writeString(packageLabel);
        parcel.writeString(packageName);
        parcel.writeString(versionName);
        parcel.writeString(versionCode);
        parcel.writeString(data);
        parcel.writeString(source);
        parcel.writeString(install);
        parcel.writeString(update);
        parcel.writeBooleanArray(new boolean[]{ system, disable, hide, favorite });
    }

    private static class AppItemSerializer extends Serializer<AppItem> {
        @Override
        public void serialize(SerializationContext context, SerializerOutput output, AppItem object) throws IOException {

            //serializeBitmap(output,object); The CacheFile will be to large. Storing it like that
            output.writeString(object.packageLabel);
            output.writeString(object.packageName);
            output.writeString(object.versionName);
            output.writeString(object.versionCode);
            output.writeString(object.data);
            output.writeString(object.source);
            output.writeString(object.install);
            output.writeString(object.update);
            output.writeBoolean(object.system);
            output.writeBoolean(object.disable);
            output.writeBoolean(object.hide);
            output.writeBoolean(object.favorite);
        }

        /*
        private void serializeBitmap(SerializerOutput output, AppItem object) throws IOException {
            int [] pixels;
            int width , height;
            width = object.icon.getWidth();
            height = object.icon.getHeight();
            pixels = new int [width*height];
            object.icon.getPixels(pixels,0,width,0,0,width,height);

            ByteBuffer byteBuffer = ByteBuffer.allocate(pixels.length * 4);
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(pixels);
            byte[] array = byteBuffer.array();

            output.writeInt(width);
            output.writeInt(height);
            output.writeByteArray(array);
        }

        private Bitmap deserializeBitmap(SerializerInput input) throws IOException {
            int[] pixels;
            int width, height;
            width = input.readInt();
            height = input.readInt();

            byte[] tha_pixels = input.readByteArray();
            IntBuffer intBuffer = ByteBuffer.wrap(tha_pixels)
                    .order(ByteOrder.BIG_ENDIAN)
                    .asIntBuffer();
            pixels = new int[intBuffer.remaining()];
            intBuffer.get(pixels);
            return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        }
        */

        @Override
        public AppItem deserialize(SerializationContext context, SerializerInput input) throws IOException, ClassNotFoundException {

            Bitmap icon;
            String packageLabel;
            String packageName;
            String versionName;
            String versionCode;
            String data;
            String source;
            String install;
            String update;
            boolean system;
            boolean disable;
            boolean hide;
            boolean favorite;

            icon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            // icon = deserializeBitmap(input);
            packageLabel = input.readString();
            packageName = input.readString();
            versionName = input.readString();
            versionCode = input.readString();
            data = input.readString();
            source = input.readString();
            install = input.readString();
            update = input.readString();
            system = input.readBoolean();
            disable = input.readBoolean();
            hide = input.readBoolean();
            favorite = input.readBoolean();


            return new AppItem(
                    icon,
                    packageLabel,
                    packageName,
                    versionName,
                    versionCode,
                    data,
                    source,
                    install,
                    update,
                    system,
                    disable,
                    hide,
                    favorite);
        }
    }
}
