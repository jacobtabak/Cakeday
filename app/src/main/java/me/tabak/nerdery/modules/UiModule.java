package me.tabak.nerdery.modules;


import dagger.Module;
import me.tabak.nerdery.ui.fragments.RedditLinkListFragment;

@Module(
    complete = false,
    library = true,
    injects = {
        RedditLinkListFragment.class
    }
)
public class UiModule {
}
