package com.rockthejvm.day2.asciiart.imagescaler

import java.awt.RenderingHints
import java.awt.image.BufferedImage


trait ImageScaler {

  val maxSensibleWidth: Int
  val maxSensibleHeight: Int

  def scale(image: BufferedImage, widthSetting: Option[Int]): BufferedImage = {
    val (width, height) = chooseDimensions(image, widthSetting)
		val scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		val gfx = scaledImage.createGraphics()
		gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		gfx.drawImage(image, 0, 0, width, height, null)
		gfx.dispose()
		scaledImage
	}

  /*
    TODO 4: determine the width and height of the final "image" (in char count)
       based on the dimensions of the original image (in pixels).

    If the image is smaller than maxSensibleWidth x maxSensibleHeight,
      then just return the dimensions of the original image (every pixel = one char).
    Otherwise, scale the dimensions so they keeps its aspect ratio
      and fit in maxSensibleWidth x maxSensibleHeight, and return the resulting values.

    You might need calcResizedHeight (if width is too big) and/or calcResizedWidth (if height is too big), implemented below.
  */
  private def chooseDimensions(image: BufferedImage, widthSetting: Option[Int]): (Int, Int) = ???

  private def calcResizedHeight(image: BufferedImage, resizedWidth: Int): Int =
    ((resizedWidth.toDouble / image.getWidth) * image.getHeight).toInt
  
  private def calcResizedWidth(image: BufferedImage, resizedHeight: Int): Int =
    ((resizedHeight.toDouble / image.getHeight) * image.getWidth).toInt
}
