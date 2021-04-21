package com.rockthejvm.day2.asciiart.asciifier

import java.awt.Color
import java.awt.image.BufferedImage

class PlainTextAsciifier extends Asciifier  {

  /**
    * Converts an image into a String containing H x W characters, one for each pixel in the image.
    *
    * @param image the input image as a BufferedImage
    * @return the String representation
    */
  def asciify(image: BufferedImage): String = {
    val pixels = mapImage(image)(x => chooseChar(rgbMax(x)))
    pixels.map(x => s"${x.mkString(" ")}\n").mkString
  }

}