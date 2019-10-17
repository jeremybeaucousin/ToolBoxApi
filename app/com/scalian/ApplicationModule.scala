package com.scalian

import com.google.inject.{AbstractModule, Provides}
import play.api.Environment

import com.hhandoko.play.pdf.PdfGenerator

class ApplicationModule extends AbstractModule {
  
  override def configure(): Unit = {}
  
  @Provides
  def providePdfGenerator(env: Environment): PdfGenerator = {
    val pdfGen = new PdfGenerator(env)
    pdfGen
  }
}