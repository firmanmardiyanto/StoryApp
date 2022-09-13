package org.firmanmardiyanto.storyapp.stackwidget

import android.content.Intent
import android.widget.RemoteViewsService
import org.firmanmardiyanto.core.domain.usecase.StoryUseCase
import org.firmanmardiyanto.storyapp.StoryListWidget
import org.koin.android.ext.android.inject

class StackWidgetService : RemoteViewsService() {
    private val storyUseCase by inject<StoryUseCase>()
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        StoryListWidget.StackRemoteViewsFactory(this.applicationContext, storyUseCase)
}