import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;

import androidx.annotation.WorkerThread;

public class IconPackProvider {

    private final Context mContext;
    private final String mIconPackAuthority;
    private final ProviderInfo mProviderInfo;
    private final PackageManager mPackageMgr;
    private Map<String, IconPackInfo> mIconPacks;

    public IconPackProvider(Context context, String providerAuthority) {
        mContext = context;
        // register launcher iconpack provider
        Intent homeIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);

        ResolveInfo info = context.getPackageManager().resolveActivity(homeIntent,
                PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_META_DATA);
        if (info != null && info.activityInfo != null && info.activityInfo.metaData != null) {
            mIconPackAuthority = info.activityInfo.metaData.getString(providerAuthority);
        } else {
            mIconPackAuthority = null;
        }
        mProviderInfo = TextUtils.isEmpty(mGridProviderAuthority) ? null
                : mContext.getPackageManager().resolveContentProvider(mIconPackAuthority, 0);

        mIconPacks = new HashMap<>();
        loadAvailableIconPacks();
    }

    @WorkerThread
    private void loadAvailableIconPacks() {
         List<ResolveInfo> list;
         list = mPackageMgr.queryIntentActivities(new Intent("com.novalauncher.THEME"), 0);
         list.addAll(mPackageMgr.queryIntentActivities(new Intent("org.adw.launcher.icons.ACTION_PICK_ICON"), 0));
         list.addAll(mPackageMgr.queryIntentActivities(new Intent("com.dlto.atom.launcher.THEME"), 0));
         list.addAll(mPackageMgr.queryIntentActivities(new Intent("android.intent.action.MAIN").addCategory("com.anddoes.launcher.THEME"), 0));
         for (ResolveInfo info : list) {
             mIconPacks.put(info.activityInfo.packageName, new IconPackInfo(info, mPackageMgr));
         }
     }

    private static class IconPackInfo {
         String packageName;
         CharSequence label;
         Drawable icon;

         IconPackInfo(ResolveInfo r, PackageManager packageManager) {
             packageName = r.activityInfo.packageName;
             icon = r.loadIcon(packageManager);
             label = r.loadLabel(packageManager);
         }

         public IconPackInfo(String label, Drawable icon, String packageName) {
             this.label = label;
             this.icon = icon;
             this.packageName = packageName;
         }
     }
}
