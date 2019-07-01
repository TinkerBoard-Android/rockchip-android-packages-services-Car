/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.car.developeroptions.display;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;

import com.android.car.developeroptions.R;
import com.android.car.developeroptions.dashboard.DashboardFragment;
import com.android.car.developeroptions.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.android.settingslib.widget.FooterPreference;

import java.util.Arrays;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class AdaptiveSleepSettings extends DashboardFragment {

    private static final String TAG = "AdaptiveSleepSettings";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        final FooterPreference footerPreference =
                mFooterPreferenceMixin.createFooterPreference();
        footerPreference.setIcon(R.drawable.ic_privacy_shield_24dp);
        footerPreference.setTitle(R.string.adaptive_sleep_privacy);
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.adaptive_sleep_detail;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.SETTINGS_ADAPTIVE_SLEEP;
    }

    @Override
    public int getHelpResource() {
        return R.string.help_url_adaptive_sleep;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(
                        Context context, boolean enabled) {
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.adaptive_sleep_detail;
                    return Arrays.asList(sir);
                }
            };
}
