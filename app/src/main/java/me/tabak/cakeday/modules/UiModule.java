package me.tabak.cakeday.modules;


import dagger.Module;
import me.tabak.cakeday.ui.fragments.RedditLinkDetailFragment;
import me.tabak.cakeday.ui.fragments.RedditLinkListFragment;

@Module(
    complete = false,
    library = true,
    injects = {
        RedditLinkListFragment.class,
        RedditLinkDetailFragment.class
    }
)
public class UiModule {
}
