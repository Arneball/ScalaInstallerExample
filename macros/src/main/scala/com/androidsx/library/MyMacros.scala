package com.androidsx.library

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object MyMacros {
  def trusta: String = macro trust_impl

  def trust_impl(c: blackbox.Context): c.Expr[String] = {
    import c.universe._
    c.Expr{
      q""""Reified string""""
    }
  }

  def debug(params: Any*): Unit = macro debug_impl

  def debug_impl(c: blackbox.Context)(params: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val sbTree = params.map{ p =>
      val (lhs, middle, rhs, typ, slask) = (q"""${show(p.tree)}""", q"""" = """", q"$p.toString", q"${show(p.actualType)}", q"""", """")
      val delim = q"""": """"
      q"$lhs + $middle + $rhs + $delim + $typ + $slask"
    }.foldLeft(q"new StringBuilder"){ case (acc, elem) => q"$acc append $elem"}
    c.Expr[Unit]{
      q"""android.util.Log.d("logTag", $sbTree.toString)"""
    }
  }
}