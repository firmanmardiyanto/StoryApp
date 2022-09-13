package org.firmanmardiyanto.storyapp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import org.firmanmardiyanto.core.data.Resource
import org.firmanmardiyanto.core.domain.usecase.StoryUseCase
import org.firmanmardiyanto.storyapp.stackwidget.StackWidgetService


class StoryListWidget : AppWidgetProvider() {

    companion object {

        private const val TOAST_ACTION = "TOAST_ACTION"
        const val EXTRA_ITEM = "EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName, R.layout.story_list_widget)
            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)

            val toastIntent = Intent(context, StoryListWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val toastPendingIntent = PendingIntent.getBroadcast(
                context, 0, toastIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            )

            views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != null) {
            if (intent.action == TOAST_ACTION) {
                val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
                Toast.makeText(
                    context,
                    context.getString(R.string.touched_view_with_id, viewIndex.toString()),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    internal class StackRemoteViewsFactory(
        private val mContext: Context,
        private val storyUseCase: StoryUseCase
    ) :
        RemoteViewsService.RemoteViewsFactory {

        private val mWidgetItems = ArrayList<Bitmap>()
        private val mWidgetUrlItems = ArrayList<String>()

        override fun onCreate() {
            storyUseCase.getStories().asLiveData().observeForever {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { stories ->
                            stories.map { story ->
                                mWidgetUrlItems.add(story.photoUrl)
                            }
                        }
                    }
                    is Resource.Error -> mWidgetUrlItems.clear()
                    is Resource.Loading -> mWidgetUrlItems.clear()
                }
            }
        }

        override fun onDataSetChanged() {
            mWidgetUrlItems.map {
                val futureTarget: FutureTarget<Bitmap> =
                    Glide.with(mContext)
                        .asBitmap()
                        .load(it)
                        .submit()
                futureTarget.get()?.let { bitmap ->
                    mWidgetItems.add(bitmap)
                }
            }
        }

        override fun onDestroy() {
            mWidgetItems.clear()
            mWidgetUrlItems.clear()
        }

        override fun getCount(): Int = mWidgetItems.size

        override fun getViewAt(position: Int): RemoteViews {
            val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
            rv.setImageViewBitmap(R.id.iv_widget, mWidgetItems[position])

            val extras = bundleOf(
                EXTRA_ITEM to position
            )

            val fillInIntent = Intent()
            fillInIntent.putExtras(extras)

            rv.setOnClickFillInIntent(R.id.iv_widget, fillInIntent)
            return rv
        }

        override fun getLoadingView(): RemoteViews? = null

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(position: Int): Long = position.toLong()

        override fun hasStableIds(): Boolean = false
    }
}

