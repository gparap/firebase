/*
 * Copyright 2021 gparap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gparap.apps.chat.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import gparap.apps.chat.R;
import gparap.apps.chat.ui.private_chat.PrivateChatFragment;
import gparap.apps.chat.ui.public_chat.PublicChatFragment;
import gparap.apps.chat.ui.settings.SettingsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENTS_COUNT = 3;
    private final Context context;

    public ViewPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PrivateChatFragment.newInstance();
            case 1:
                return PublicChatFragment.newInstance();
            case 2:
                return SettingsFragment.newInstance();
            default:
                throw new IllegalStateException("DEBUG: this shouldn't have happened..");
        }
    }

    @Override
    public int getCount() {
        return FRAGMENTS_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.title_private_chat);
            case 1:
                return context.getResources().getString(R.string.title_public_chat);
            case 2:
                return context.getResources().getString(R.string.title_settings);
            default:
                throw new IllegalStateException("DEBUG: this shouldn't have happened..");
        }
    }
}
