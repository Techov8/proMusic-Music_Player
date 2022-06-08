/*
 * Copyright (c) 2020 Hemanth Savarla.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package io.github.muntashirakon.music.fragments.home

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.monkey.appthemehelper.common.ATHToolbarActivity
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import io.github.muntashirakon.music.*
import io.github.muntashirakon.music.activities.OnlineMusicActivity
import io.github.muntashirakon.music.adapter.HomeAdapter
import io.github.muntashirakon.music.dialogs.CreatePlaylistDialog
import io.github.muntashirakon.music.dialogs.ImportPlaylistDialog
import io.github.muntashirakon.music.fragments.base.AbsMainActivityFragment
import io.github.muntashirakon.music.glide.UserProfileGlideRequest
import io.github.muntashirakon.music.util.NavigationUtil
import io.github.muntashirakon.music.util.PreferenceUtil
import kotlinx.android.synthetic.main.abs_playlists.*
import kotlinx.android.synthetic.main.fragment_banner_home.*
import kotlinx.android.synthetic.main.home_content.*

class HomeFragment :
    AbsMainActivityFragment(if (PreferenceUtil.isHomeBanner) R.layout.fragment_banner_home else R.layout.fragment_banner_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.setBottomBarVisibility(View.VISIBLE)
        mainActivity.setSupportActionBar(toolbar)
        mainActivity.supportActionBar?.title = null
        setStatusBarColorAuto(view)
        bannerImage?.setOnClickListener {
//            val options = ActivityOptions.makeSceneTransitionAnimation(
//                mainActivity,
//                userImage,
//                getString(R.string.transition_user_image)
//            )
//            NavigationUtil.goToUserInfo(requireActivity(), options)

            val viewIntent = Intent(
                "android.intent.action.VIEW",
                Uri.parse(DBQueries.bannerUrl)
            )
            startActivity(viewIntent)
        }

        lastAdded.setOnClickListener {
            findNavController().navigate(
                R.id.detailListFragment,
                bundleOf("type" to LAST_ADDED_PLAYLIST)
            )
        }

        topPlayed.setOnClickListener {
            findNavController().navigate(
                R.id.detailListFragment,
                bundleOf("type" to TOP_PLAYED_PLAYLIST)
            )
        }

        actionShuffle.setOnClickListener {
            libraryViewModel.shuffleSongs()
        }

        history.setOnClickListener {
            findNavController().navigate(
                R.id.detailListFragment,
                bundleOf("type" to HISTORY_PLAYLIST)
            )
        }

        userImage.setOnClickListener {
            val options = ActivityOptions.makeSceneTransitionAnimation(
                mainActivity,
                userImage,
                getString(R.string.transition_user_image)
            )
            NavigationUtil.goToUserInfo(requireActivity(), options)
        }
        titleWelcome?.text = String.format("%s", PreferenceUtil.userName)

        val homeAdapter = HomeAdapter(mainActivity)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(mainActivity)
            adapter = homeAdapter
        }

        libraryViewModel.getHome().observe(viewLifecycleOwner, Observer {
            homeAdapter.swapData(it)
        })
        image_toolbar.setOnClickListener{
            startActivity(Intent(requireActivity(), OnlineMusicActivity::class.java))
        }

        loadProfile()
        setupTitle()
       // Glide.with(requireActivity()).load(DBQueries.banner).into(bannerImage)
    }

    private fun setupTitle() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(
                R.id.searchFragment,
                null,
                navOptions
            )
        }
        appNameText.text = getString(R.string.app_name)
    }

    private fun loadProfile() {
        if(DBQueries.banner==null) {

            DBQueries.firebaseFirestore.collection("HOME").document("data").get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    DBQueries.bannerUrl = task.result!!
                        .getString("banner_url")
                    DBQueries.banner = task.result!!.getString("banner_image")
                    DBQueries.musicUrl = task.result!!.getString("music_url")
                    DBQueries.isMusicActive = task.result!!.getBoolean("is_music_active")!!
                    Glide.with(requireActivity()).load(DBQueries.banner).into(bannerImage)
                }

        }else{
            Glide.with(requireActivity()).load(DBQueries.banner).into(bannerImage)
        }




        UserProfileGlideRequest.Builder.from(
            Glide.with(requireActivity()),
            UserProfileGlideRequest.getUserModel()
        ).build().into(userImage)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        menu.removeItem(R.id.action_grid_size)
        menu.removeItem(R.id.action_layout_type)
        menu.removeItem(R.id.action_sort_order)
        menu.findItem(R.id.action_settings).setShowAsAction(SHOW_AS_ACTION_IF_ROOM)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireContext(),
            toolbar,
            menu,
            ATHToolbarActivity.getToolbarBackgroundColor(toolbar)
        )
    }

    companion object {

        const val TAG: String = "BannerHomeFragment"

        @JvmStatic
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> findNavController().navigate(
                R.id.settingsActivity,
                null,
                navOptions
            )
            R.id.action_import_playlist -> ImportPlaylistDialog().show(
                childFragmentManager,
                "ImportPlaylist"
            )
            R.id.action_add_to_playlist -> CreatePlaylistDialog.create(emptyList()).show(
                childFragmentManager,
                "ShowCreatePlaylistDialog"
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(requireActivity(), toolbar)
    }
}
