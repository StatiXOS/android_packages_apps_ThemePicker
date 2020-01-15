import com.android.wallpaper.picker.ToolbarFragment;

public class IconPackFragment extends ToolBarFragment {

    public IconPackManager getIconPackManager() {
        return null;
    }

    /**
     * Interface to be implemented by an Activity hosting a {@link ThemeFragment}
     */
    public interface ThemeFragmentHost {
        ThemeManager getThemeManager();
    }

    /** The fragment host. */
    public class IconPackFragmentHost {
        public IconPackFragmentHost() {
        }
    }
}
