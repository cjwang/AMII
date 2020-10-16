package io.unthrottled.amii.onboarding

import com.intellij.notification.Notification
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.notification.impl.NotificationsManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.util.Disposer
import com.intellij.ui.BalloonLayoutData
import io.unthrottled.amii.config.Constants.PLUGIN_ICON
import io.unthrottled.amii.config.Constants.PLUGIN_NAME
import io.unthrottled.amii.tools.BalloonTools.fetchBalloonParameters

@Suppress("MaxLineLength")
val UPDATE_MESSAGE: String =
  """
      What's New?<br>
      <ul>
      </ul>
      <br>Please see the <a href="https://github.com/Unthrottled/AMII/blob/master/CHANGELOG.md">changelog</a> for more details.
      <br><br>
  """.trimIndent()

object UpdateNotification {

  private const val UPDATE_CHANNEL_NAME = "$PLUGIN_NAME Updates"
  private val notificationGroup = NotificationGroup(
    UPDATE_CHANNEL_NAME,
    NotificationDisplayType.STICKY_BALLOON,
    false,
    UPDATE_CHANNEL_NAME
  )

  fun display(
    project: Project,
    newVersion: String
  ) {
    val updateNotification = notificationGroup.createNotification(
      "$PLUGIN_NAME updated to v$newVersion",
      UPDATE_MESSAGE,
      NotificationType.INFORMATION
    )
      .setIcon(PLUGIN_ICON)
      .setListener(NotificationListener.UrlOpeningListener(false))

    showNotification(project, updateNotification)
  }

  fun sendMessage(
    title: String,
    message: String,
    project: Project? = null
  ) {
    showRegularNotification(
      title,
      message,
      project = project,
      listener = defaultListener
    )
  }

  private val defaultListener = NotificationListener.UrlOpeningListener(false)

  private fun showRegularNotification(
    title: String = "",
    content: String,
    project: Project? = null,
    listener: NotificationListener? = defaultListener
  ) {
    notificationGroup.createNotification(
      title,
      content,
      listener = listener
    ).setIcon(PLUGIN_ICON)
      .notify(project)
  }

  private fun showNotification(
    project: Project,
    updateNotification: Notification
  ) {
    try {
      val (ideFrame, notificationPosition) = fetchBalloonParameters(project)
      val balloon = NotificationsManagerImpl.createBalloon(
        ideFrame,
        updateNotification,
        true,
        false,
        BalloonLayoutData.fullContent(),
        Disposer.newDisposable()
      )
      balloon.show(notificationPosition, Balloon.Position.atLeft)
    } catch (e: Throwable) {
      updateNotification.notify(project)
    }
  }
}