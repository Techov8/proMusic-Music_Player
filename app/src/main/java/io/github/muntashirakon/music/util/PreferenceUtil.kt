package io.github.muntashirakon.music.util

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import io.github.muntashirakon.music.ADAPTIVE_COLOR_APP
import io.github.muntashirakon.music.ALBUM_ARTISTS_ONLY
import io.github.muntashirakon.music.ALBUM_ART_ON_LOCK_SCREEN
import io.github.muntashirakon.music.ALBUM_COVER_STYLE
import io.github.muntashirakon.music.ALBUM_COVER_TRANSFORM
import io.github.muntashirakon.music.ALBUM_DETAIL_SONG_SORT_ORDER
import io.github.muntashirakon.music.ALBUM_GRID_SIZE
import io.github.muntashirakon.music.ALBUM_GRID_SIZE_LAND
import io.github.muntashirakon.music.ALBUM_GRID_STYLE
import io.github.muntashirakon.music.ALBUM_SONG_SORT_ORDER
import io.github.muntashirakon.music.ALBUM_SORT_ORDER
import io.github.muntashirakon.music.ARTIST_ALBUM_SORT_ORDER
import io.github.muntashirakon.music.ARTIST_GRID_SIZE
import io.github.muntashirakon.music.ARTIST_GRID_SIZE_LAND
import io.github.muntashirakon.music.ARTIST_GRID_STYLE
import io.github.muntashirakon.music.ARTIST_SONG_SORT_ORDER
import io.github.muntashirakon.music.ARTIST_SORT_ORDER
import io.github.muntashirakon.music.AUDIO_DUCKING
import io.github.muntashirakon.music.AUTO_DOWNLOAD_IMAGES_POLICY
import io.github.muntashirakon.music.App
import io.github.muntashirakon.music.BLACK_THEME
import io.github.muntashirakon.music.BLUETOOTH_PLAYBACK
import io.github.muntashirakon.music.BLURRED_ALBUM_ART
import io.github.muntashirakon.music.CAROUSEL_EFFECT
import io.github.muntashirakon.music.CHOOSE_EQUALIZER
import io.github.muntashirakon.music.CLASSIC_NOTIFICATION
import io.github.muntashirakon.music.COLORED_APP_SHORTCUTS
import io.github.muntashirakon.music.COLORED_NOTIFICATION
import io.github.muntashirakon.music.DESATURATED_COLOR
import io.github.muntashirakon.music.EXPAND_NOW_PLAYING_PANEL
import io.github.muntashirakon.music.EXTRA_SONG_INFO
import io.github.muntashirakon.music.FILTER_SONG
import io.github.muntashirakon.music.GAP_LESS_PLAYBACK
import io.github.muntashirakon.music.GENERAL_THEME
import io.github.muntashirakon.music.GENRE_SORT_ORDER
import io.github.muntashirakon.music.HOME_ALBUM_GRID_STYLE
import io.github.muntashirakon.music.HOME_ARTIST_GRID_STYLE
import io.github.muntashirakon.music.IGNORE_MEDIA_STORE_ARTWORK
import io.github.muntashirakon.music.INITIALIZED_BLACKLIST
import io.github.muntashirakon.music.KEEP_SCREEN_ON
import io.github.muntashirakon.music.LANGUAGE_NAME
import io.github.muntashirakon.music.LAST_ADDED_CUTOFF
import io.github.muntashirakon.music.LAST_CHANGELOG_VERSION
import io.github.muntashirakon.music.LAST_PAGE
import io.github.muntashirakon.music.LAST_SLEEP_TIMER_VALUE
import io.github.muntashirakon.music.LIBRARY_CATEGORIES
import io.github.muntashirakon.music.LOCK_SCREEN
import io.github.muntashirakon.music.LYRICS_OPTIONS
import io.github.muntashirakon.music.NEXT_SLEEP_TIMER_ELAPSED_REALTIME
import io.github.muntashirakon.music.NOW_PLAYING_SCREEN_ID
import io.github.muntashirakon.music.PAUSE_ON_ZERO_VOLUME
import io.github.muntashirakon.music.PLAYLIST_SORT_ORDER
import io.github.muntashirakon.music.R
import io.github.muntashirakon.music.RECENTLY_PLAYED_CUTOFF
import io.github.muntashirakon.music.SAF_SDCARD_URI
import io.github.muntashirakon.music.SLEEP_TIMER_FINISH_SONG
import io.github.muntashirakon.music.SONG_GRID_SIZE
import io.github.muntashirakon.music.SONG_GRID_SIZE_LAND
import io.github.muntashirakon.music.SONG_GRID_STYLE
import io.github.muntashirakon.music.SONG_SORT_ORDER
import io.github.muntashirakon.music.START_DIRECTORY
import io.github.muntashirakon.music.TAB_TEXT_MODE
import io.github.muntashirakon.music.TOGGLE_ADD_CONTROLS
import io.github.muntashirakon.music.TOGGLE_FULL_SCREEN
import io.github.muntashirakon.music.TOGGLE_HEADSET
import io.github.muntashirakon.music.TOGGLE_HOME_BANNER
import io.github.muntashirakon.music.TOGGLE_SHUFFLE
import io.github.muntashirakon.music.TOGGLE_VOLUME
import io.github.muntashirakon.music.USER_NAME
import io.github.muntashirakon.music.extensions.getIntRes
import io.github.muntashirakon.music.extensions.getStringOrDefault
import io.github.muntashirakon.music.fragments.AlbumCoverStyle
import io.github.muntashirakon.music.fragments.NowPlayingScreen
import io.github.muntashirakon.music.fragments.folder.FoldersFragment
import io.github.muntashirakon.music.helper.SortOrder.*
import io.github.muntashirakon.music.model.CategoryInfo
import io.github.muntashirakon.music.transform.CascadingPageTransformer
import io.github.muntashirakon.music.transform.DepthTransformation
import io.github.muntashirakon.music.transform.HingeTransformation
import io.github.muntashirakon.music.transform.HorizontalFlipTransformation
import io.github.muntashirakon.music.transform.NormalPageTransformer
import io.github.muntashirakon.music.transform.VerticalFlipTransformation
import io.github.muntashirakon.music.transform.VerticalStackTransformer
import io.github.muntashirakon.music.util.theme.ThemeMode
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File


object PreferenceUtil {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext())

    val defaultCategories = listOf(
        CategoryInfo(CategoryInfo.Category.Home, true),
        CategoryInfo(CategoryInfo.Category.Songs, true),
        CategoryInfo(CategoryInfo.Category.Albums, true),
        CategoryInfo(CategoryInfo.Category.Artists, true),
        CategoryInfo(CategoryInfo.Category.Playlists, true),
        CategoryInfo(CategoryInfo.Category.Genres, false),
        CategoryInfo(CategoryInfo.Category.Folder, false)
    )

    var libraryCategory: List<CategoryInfo>
        get() {
            val gson = Gson()
            val collectionType = object : TypeToken<List<CategoryInfo>>() {}.type

            val data = sharedPreferences.getStringOrDefault(
                LIBRARY_CATEGORIES,
                gson.toJson(defaultCategories, collectionType)
            )
            return try {
                Gson().fromJson(data, collectionType)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                return defaultCategories
            }
        }
        set(value) {
            val collectionType = object : TypeToken<List<CategoryInfo?>?>() {}.type
            sharedPreferences.edit {
                putString(LIBRARY_CATEGORIES, Gson().toJson(value, collectionType))
            }
        }

    fun registerOnSharedPreferenceChangedListener(
        listener: OnSharedPreferenceChangeListener
    ) = sharedPreferences.registerOnSharedPreferenceChangeListener(listener)


    fun unregisterOnSharedPreferenceChangedListener(
        changeListener: OnSharedPreferenceChangeListener
    ) = sharedPreferences.unregisterOnSharedPreferenceChangeListener(changeListener)


    val baseTheme get() = sharedPreferences.getStringOrDefault(GENERAL_THEME, "auto")

    fun getGeneralThemeValue(isSystemDark: Boolean): ThemeMode {
        val themeMode: String =
            sharedPreferences.getStringOrDefault(GENERAL_THEME, "auto")
        return if (isBlackMode && isSystemDark) {
            ThemeMode.BLACK
        } else {
            if (isBlackMode && themeMode == "dark") {
                ThemeMode.BLACK
            } else {
                when (themeMode) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    "auto" -> ThemeMode.AUTO
                    else -> ThemeMode.AUTO
                }
            }
        }
    }

    val languageCode: String get() = sharedPreferences.getString(LANGUAGE_NAME, "auto") ?: "auto"

    var userName
        get() = sharedPreferences.getString(
            USER_NAME,
            App.getContext().getString(R.string.user_name)
        )
        set(value) = sharedPreferences.edit {
            putString(USER_NAME, value)
        }

    var safSdCardUri
        get() = sharedPreferences.getStringOrDefault(SAF_SDCARD_URI, "")
        set(value) = sharedPreferences.edit {
            putString(SAF_SDCARD_URI, value)
        }


    val selectedEqualizer
        get() = sharedPreferences.getStringOrDefault(
            CHOOSE_EQUALIZER,
            "system"
        )

    val autoDownloadImagesPolicy
        get() = sharedPreferences.getStringOrDefault(
            AUTO_DOWNLOAD_IMAGES_POLICY,
            "only_wifi"
        )

    var albumArtistsOnly
        get() = sharedPreferences.getBoolean(
            ALBUM_ARTISTS_ONLY,
            false
        )
        set(value) = sharedPreferences.edit { putBoolean(ALBUM_ARTISTS_ONLY, value) }

    var albumDetailSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_DETAIL_SONG_SORT_ORDER,
            AlbumSongSortOrder.SONG_TRACK_LIST
        )
        set(value) = sharedPreferences.edit { putString(ALBUM_DETAIL_SONG_SORT_ORDER, value) }

    var songSortOrder
        get() = sharedPreferences.getStringOrDefault(
            SONG_SORT_ORDER,
            SongSortOrder.SONG_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(SONG_SORT_ORDER, value)
        }

    var albumSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_SORT_ORDER,
            AlbumSortOrder.ALBUM_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(ALBUM_SORT_ORDER, value)
        }


    var artistSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_SORT_ORDER,
            ArtistSortOrder.ARTIST_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(ARTIST_SORT_ORDER, value)
        }

    val albumSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_SONG_SORT_ORDER,
            AlbumSongSortOrder.SONG_TRACK_LIST
        )

    val artistSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_SONG_SORT_ORDER,
            AlbumSongSortOrder.SONG_TRACK_LIST
        )

    val artistAlbumSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_ALBUM_SORT_ORDER,
            ArtistAlbumSortOrder.ALBUM_A_Z
        )

    var playlistSortOrder
        get() = sharedPreferences.getStringOrDefault(
            PLAYLIST_SORT_ORDER,
            PlaylistSortOrder.PLAYLIST_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(PLAYLIST_SORT_ORDER, value)
        }

    val genreSortOrder
        get() = sharedPreferences.getStringOrDefault(
            GENRE_SORT_ORDER,
            GenreSortOrder.GENRE_A_Z
        )

    val isIgnoreMediaStoreArtwork
        get() = sharedPreferences.getBoolean(
            IGNORE_MEDIA_STORE_ARTWORK,
            false
        )

    val isVolumeVisibilityMode
        get() = sharedPreferences.getBoolean(
            TOGGLE_VOLUME, false
        )

    var isInitializedBlacklist
        get() = sharedPreferences.getBoolean(
            INITIALIZED_BLACKLIST, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(INITIALIZED_BLACKLIST, value)
        }

    private val isBlackMode
        get() = sharedPreferences.getBoolean(
            BLACK_THEME, false
        )

    val isExtraControls
        get() = sharedPreferences.getBoolean(
            TOGGLE_ADD_CONTROLS, false
        )

    val isHomeBanner
        get() = sharedPreferences.getBoolean(
            TOGGLE_HOME_BANNER, true
        )
    var isClassicNotification
        get() = sharedPreferences.getBoolean(CLASSIC_NOTIFICATION, false)
        set(value) = sharedPreferences.edit { putBoolean(CLASSIC_NOTIFICATION, value) }

    val isScreenOnEnabled get() = sharedPreferences.getBoolean(KEEP_SCREEN_ON, false)

    val isShuffleModeOn get() = sharedPreferences.getBoolean(TOGGLE_SHUFFLE, false)

    val isSongInfo get() = sharedPreferences.getBoolean(EXTRA_SONG_INFO, false)

    val isPauseOnZeroVolume get() = sharedPreferences.getBoolean(PAUSE_ON_ZERO_VOLUME, false)

    var isSleepTimerFinishMusic
        get() = sharedPreferences.getBoolean(
            SLEEP_TIMER_FINISH_SONG, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(SLEEP_TIMER_FINISH_SONG, value)
        }

    val isExpandPanel get() = sharedPreferences.getBoolean(EXPAND_NOW_PLAYING_PANEL, false)

    val isHeadsetPlugged
        get() = sharedPreferences.getBoolean(
            TOGGLE_HEADSET, false
        )

    val isAlbumArtOnLockScreen
        get() = sharedPreferences.getBoolean(
            ALBUM_ART_ON_LOCK_SCREEN, false
        )

    val isAudioDucking
        get() = sharedPreferences.getBoolean(
            AUDIO_DUCKING, true
        )

    val isBluetoothSpeaker
        get() = sharedPreferences.getBoolean(
            BLUETOOTH_PLAYBACK, false
        )

    val isBlurredAlbumArt
        get() = sharedPreferences.getBoolean(
            BLURRED_ALBUM_ART, false
        )

    val isCarouselEffect
        get() = sharedPreferences.getBoolean(
            CAROUSEL_EFFECT, false
        )

    var isColoredAppShortcuts
        get() = sharedPreferences.getBoolean(
            COLORED_APP_SHORTCUTS, true
        )
        set(value) = sharedPreferences.edit {
            putBoolean(COLORED_APP_SHORTCUTS, value)
        }

    var isColoredNotification
        get() = sharedPreferences.getBoolean(
            COLORED_NOTIFICATION, true
        )
        set(value) = sharedPreferences.edit {
            putBoolean(COLORED_NOTIFICATION, value)
        }

    var isDesaturatedColor
        get() = sharedPreferences.getBoolean(
            DESATURATED_COLOR, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(DESATURATED_COLOR, value)
        }

    val isGapLessPlayback
        get() = sharedPreferences.getBoolean(
            GAP_LESS_PLAYBACK, false
        )

    val isAdaptiveColor
        get() = sharedPreferences.getBoolean(
            ADAPTIVE_COLOR_APP, false
        )

    val isFullScreenMode
        get() = sharedPreferences.getBoolean(
            TOGGLE_FULL_SCREEN, false
        )

    val isLockScreen get() = sharedPreferences.getBoolean(LOCK_SCREEN, false)

    fun isAllowedToDownloadMetadata(): Boolean {
        return when (autoDownloadImagesPolicy) {
            "always" -> true
            "only_wifi" -> {
                val connectivityManager = ContextCompat.getSystemService(
                    App.getContext(),
                    ConnectivityManager::class.java
                )
                var netInfo: NetworkInfo? = null
                if (connectivityManager != null) {
                    netInfo = connectivityManager.activeNetworkInfo
                }
                netInfo != null && netInfo.type == ConnectivityManager.TYPE_WIFI && netInfo.isConnectedOrConnecting
            }
            "never" -> false
            else -> false
        }
    }


    var lyricsOption
        get() = sharedPreferences.getInt(LYRICS_OPTIONS, 1)
        set(value) = sharedPreferences.edit {
            putInt(LYRICS_OPTIONS, value)
        }

    var songGridStyle
        get() = sharedPreferences.getInt(SONG_GRID_STYLE, R.layout.item_grid)
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_STYLE, value)
        }

    var albumGridStyle
        get() = sharedPreferences.getInt(ALBUM_GRID_STYLE, R.layout.item_grid)
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_STYLE, value)
        }

    var artistGridStyle
        get() = sharedPreferences.getInt(ARTIST_GRID_STYLE, R.layout.item_grid_circle)
        set(value) = sharedPreferences.edit {
            putInt(ARTIST_GRID_STYLE, value)
        }

    val filterLength get() = sharedPreferences.getInt(FILTER_SONG, 20)

    var lastVersion
        get() = sharedPreferences.getInt(LAST_CHANGELOG_VERSION, 0)
        set(value) = sharedPreferences.edit {
            putInt(LAST_CHANGELOG_VERSION, value)
        }

    var lastSleepTimerValue
        get() = sharedPreferences.getInt(
            LAST_SLEEP_TIMER_VALUE,
            30
        )
        set(value) = sharedPreferences.edit {
            putInt(LAST_SLEEP_TIMER_VALUE, value)
        }

    var lastPage
        get() = sharedPreferences.getInt(LAST_PAGE, R.id.action_song)
        set(value) = sharedPreferences.edit {
            putInt(LAST_PAGE, value)
        }

    var nextSleepTimerElapsedRealTime
        get() = sharedPreferences.getInt(
            NEXT_SLEEP_TIMER_ELAPSED_REALTIME,
            -1
        )
        set(value) = sharedPreferences.edit {
            putInt(NEXT_SLEEP_TIMER_ELAPSED_REALTIME, value)
        }

    fun themeResFromPrefValue(themePrefValue: String): Int {
        return when (themePrefValue) {
            "light" -> R.style.Theme_RetroMusic_Light
            "dark" -> R.style.Theme_RetroMusic
            else -> R.style.Theme_RetroMusic
        }
    }

    val homeArtistGridStyle: Int
        get() {
            val position = sharedPreferences.getStringOrDefault(
                HOME_ARTIST_GRID_STYLE, "0"
            ).toInt()
            val typedArray =
                App.getContext().resources.obtainTypedArray(R.array.pref_home_grid_style_layout)
            val layoutRes = typedArray.getResourceId(position, 0)
            typedArray.recycle()
            return if (layoutRes == 0) {
                R.layout.item_artist
            } else layoutRes
        }

    val homeAlbumGridStyle: Int
        get() {
            val position = sharedPreferences.getStringOrDefault(HOME_ALBUM_GRID_STYLE, "4").toInt()
            val typedArray =
                App.getContext().resources.obtainTypedArray(R.array.pref_home_grid_style_layout)
            val layoutRes = typedArray.getResourceId(position, 0)
            typedArray.recycle()
            return if (layoutRes == 0) {
                R.layout.item_artist
            } else layoutRes
        }

    val tabTitleMode: Int
        get() {
            return when (sharedPreferences.getStringOrDefault(
                TAB_TEXT_MODE, "0"
            ).toInt()) {
                1 -> LabelVisibilityMode.LABEL_VISIBILITY_LABELED
                0 -> LabelVisibilityMode.LABEL_VISIBILITY_AUTO
                2 -> LabelVisibilityMode.LABEL_VISIBILITY_SELECTED
                3 -> LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED
                else -> LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            }
        }


    var songGridSize
        get() = sharedPreferences.getInt(
            SONG_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_list_columns)
        )
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_SIZE, value)
        }

    var songGridSizeLand
        get() = sharedPreferences.getInt(
            SONG_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land)
        )
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_SIZE_LAND, value)
        }


    var albumGridSize: Int
        get() = sharedPreferences.getInt(
            ALBUM_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_grid_columns)
        )
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_SIZE, value)
        }


    var albumGridSizeLand
        get() = sharedPreferences.getInt(
            ALBUM_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land)
        )
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_SIZE_LAND, value)
        }


    var artistGridSize
        get() = sharedPreferences.getInt(
            ARTIST_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_grid_columns)
        )
        set(value) = sharedPreferences.edit {
            putInt(ARTIST_GRID_SIZE, value)
        }


    var artistGridSizeLand
        get() = sharedPreferences.getInt(
            ARTIST_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land)
        )
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_SIZE_LAND, value)
        }


    var albumCoverStyle: AlbumCoverStyle
        get() {
            val id: Int = sharedPreferences.getInt(ALBUM_COVER_STYLE, 0)
            for (albumCoverStyle in AlbumCoverStyle.values()) {
                if (albumCoverStyle.id == id) {
                    return albumCoverStyle
                }
            }
            return AlbumCoverStyle.Card
        }
        set(value) = sharedPreferences.edit { putInt(ALBUM_COVER_STYLE, value.id) }


    var nowPlayingScreen: NowPlayingScreen
        get() {
            val id: Int = sharedPreferences.getInt(NOW_PLAYING_SCREEN_ID, 0)
            for (nowPlayingScreen in NowPlayingScreen.values()) {
                if (nowPlayingScreen.id == id) {
                    return nowPlayingScreen
                }
            }
            return NowPlayingScreen.Adaptive
        }
        set(value) = sharedPreferences.edit {
            putInt(NOW_PLAYING_SCREEN_ID, value.id)
        }

    val albumCoverTransform: ViewPager.PageTransformer
        get() {
            val style = sharedPreferences.getStringOrDefault(
                ALBUM_COVER_TRANSFORM,
                "0"
            ).toInt()
            return when (style) {
                0 -> NormalPageTransformer()
                1 -> CascadingPageTransformer()
                2 -> DepthTransformation()
                3 -> HorizontalFlipTransformation()
                4 -> VerticalFlipTransformation()
                5 -> HingeTransformation()
                6 -> VerticalStackTransformer()
                else -> NormalPageTransformer()
            }
        }

    var startDirectory: File
        get() {
            val folderPath = FoldersFragment.getDefaultStartDirectory().path
            val filePath: String = sharedPreferences.getStringOrDefault(START_DIRECTORY, folderPath)
            return File(filePath)
        }
        set(value) = sharedPreferences.edit {
            putString(
                START_DIRECTORY,
                FileUtil.safeGetCanonicalPath(value)
            )
        }

    fun getRecentlyPlayedCutoffTimeMillis(): Long {
        return getCutoffTimeMillis(RECENTLY_PLAYED_CUTOFF)
    }

    fun getRecentlyPlayedCutoffText(context: Context): String? {
        return getCutoffText(RECENTLY_PLAYED_CUTOFF, context)
    }

    private fun getCutoffText(
        cutoff: String,
        context: Context
    ): String? {
        return when (sharedPreferences.getString(cutoff, "")) {
            "today" -> context.getString(R.string.today)
            "this_week" -> context.getString(R.string.this_week)
            "past_seven_days" -> context.getString(R.string.past_seven_days)
            "past_three_months" -> context.getString(R.string.past_three_months)
            "this_year" -> context.getString(R.string.this_year)
            "this_month" -> context.getString(R.string.this_month)
            else -> context.getString(R.string.this_month)
        }
    }

    private fun getCutoffTimeMillis(cutoff: String): Long {
        val calendarUtil = CalendarUtil()
        val interval: Long
        interval = when (sharedPreferences.getString(cutoff, "")) {
            "today" -> calendarUtil.elapsedToday
            "this_week" -> calendarUtil.elapsedWeek
            "past_seven_days" -> calendarUtil.getElapsedDays(7)
            "past_three_months" -> calendarUtil.getElapsedMonths(3)
            "this_year" -> calendarUtil.elapsedYear
            "this_month" -> calendarUtil.elapsedMonth
            else -> calendarUtil.elapsedMonth
        }
        return System.currentTimeMillis() - interval
    }

    val lastAddedCutoff: Long
        get() {
            val calendarUtil = CalendarUtil()
            val interval =
                when (sharedPreferences.getStringOrDefault(LAST_ADDED_CUTOFF, "this_month")) {
                    "today" -> calendarUtil.elapsedToday
                    "this_week" -> calendarUtil.elapsedWeek
                    "past_three_months" -> calendarUtil.getElapsedMonths(3)
                    "this_year" -> calendarUtil.elapsedYear
                    "this_month" -> calendarUtil.elapsedMonth
                    else -> calendarUtil.elapsedMonth
                }
            return (System.currentTimeMillis() - interval) / 1000
        }

}
