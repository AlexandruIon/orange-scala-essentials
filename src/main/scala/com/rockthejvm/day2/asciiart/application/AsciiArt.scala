package com.rockthejvm.day2.asciiart.application

import com.rockthejvm.day2.asciiart.asciifier.{Asciifier, PlainTextAsciifier}
import com.rockthejvm.day2.asciiart.imageloader.{FileLoader, ImageLoader}
import com.rockthejvm.day2.asciiart.imagescaler.{ImageScaler, PlainTextScaler}


trait AsciiArtApplication {
  val asciifier: Asciifier
  val imageLoader: ImageLoader
  val imageScaler: ImageScaler

  /**
    * Loads an image at a given path and returns its String representation.
    *
    * @param path the path of the image
    * @param width an optional maximum width; if None is passed, then the max dimensions are managed by the image scaler
    * @return a Some containing the String representation of the image, or None if the image could not be loaded
    */
  def run(path: String, width: Option[Int]): Option[String] = {
    val maybeImage = imageLoader.loadImage(path)
    // scaling (optional)
    maybeImage.map(asciifier.asciify)
  }

  /*
    You only need to use the loader, scaler and asciifier that you are instantiated with.
    1) load the image
    2) scale the image - at first, skip this and just use a small image when you test
    3) turn the scaled image into a string and return it
   */
}

object AsciiArt extends AsciiArtApplication {

  override val asciifier = new PlainTextAsciifier
  override val imageLoader = new FileLoader
  override val imageScaler = new PlainTextScaler

  def main(args: Array[String]): Unit = {
    // can read the file path from the command line
    val path = "src/main/resources/rockthejvm-small.png"
    val width = Some(100)

    val asciiImage = run(path, width)
    asciiImage.foreach(println)
  }
}


