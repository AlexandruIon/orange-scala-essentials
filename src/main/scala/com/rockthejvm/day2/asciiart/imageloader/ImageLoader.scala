package com.rockthejvm.day2.asciiart.imageloader

import java.awt.image.BufferedImage

trait ImageLoader {
  def loadImage(url: String): Option[BufferedImage]
}