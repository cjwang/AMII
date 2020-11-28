package io.unthrottled.amii.memes

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.util.ui.UIUtil
import io.unthrottled.amii.assets.MemeAssetCategory
import io.unthrottled.amii.assets.VisualAssetDefinitionService
import io.unthrottled.amii.events.UserEvent
import io.unthrottled.amii.services.ExecutionService
import io.unthrottled.amii.tools.BalloonTools.getIDEFrame
import io.unthrottled.amii.tools.doOrElse
import io.unthrottled.amii.tools.toOptional

class MemeService(private val project: Project) {

  fun createMeme(
    userEvent: UserEvent,
    memeAssetCategory: MemeAssetCategory,
    memeDecorator: (Meme.Builder) -> Meme
  ) {
    ExecutionService.executeAsynchronously {
      UIUtil.getRootPane(
        getIDEFrame(project).component
      )?.layeredPane
        .toOptional()
        .flatMap { rootPane ->
          VisualAssetDefinitionService
            .getRandomAssetByCategory(memeAssetCategory)
            .map { visualMeme ->
              memeDecorator(Meme.Builder(visualMeme, userEvent, rootPane))
            }
        }.doOrElse({
          attemptToDisplayMeme(it)
        }) {
          // todo: notify user not available
        }
    }
  }

  private fun attemptToDisplayMeme(meme: Meme) {
    // todo: only show if not showing and if greater...
    showMeme(meme)
  }

  private fun showMeme(meme: Meme) {
    ApplicationManager.getApplication().invokeLater {
      meme.display()
    }
  }
}
