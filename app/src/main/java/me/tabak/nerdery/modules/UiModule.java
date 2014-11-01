package me.tabak.nerdery.modules;


import dagger.Module;
import me.tabak.nerdery.ui.fragments.RedditLinkDetailFragment;
import me.tabak.nerdery.ui.fragments.RedditLinkListFragment;

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
