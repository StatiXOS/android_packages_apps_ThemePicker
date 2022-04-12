package com.google.android.apps.wallpaper.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

import com.android.systemui.flags.FlagManager;

public class RecentWallpapersProvider extends ContentProvider {

    @Override // android.content.ContentProvider
    @NotNull
    public String getType(@NotNull Uri uri) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        return "vnd.android.cursor.dir/recent_wallpapers";
    }

}
