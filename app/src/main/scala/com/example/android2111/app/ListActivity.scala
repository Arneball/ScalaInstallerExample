package com.example.android2111.app

import android.content.Context
import android.os.Bundle
import android.view.{LayoutInflater, ViewGroup, View}
import android.widget.{TextView, ListView, SimpleAdapter, BaseAdapter}
import com.j256.ormlite.dao.Dao
import collection.JavaConversions._
import Implicits._
class ListActivity extends ActivityExtras {
  lazy val ageField = this.gtTxt(R.id.age)
  lazy val nameField = this.gtTxt(R.id.name)
  lazy val button = this.fid(R.id.add_user)
  lazy val list = this.fid(R.id.listView).asInstanceOf[ListView]
  lazy val dao = DbAdapter.getDao[User]

  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    setContentView(R.layout.activity_list)


    button.setCl{
      ageField -> nameField match {
        case (EmptyText(), _) => toast("Age must not be empty")
        case (_, EmptyText()) => toast("Name must not be empty")
        case (age, name) =>
          dao.create(new User(page = age.getRealText.toInt, pname = name.getRealText)) match {
            case 1 =>
              toast("Success")
              initList()
            case _ => toast("Failed to add user")
          }
      }
    }

    initList()
  }

  def initList() = {
    val items = dao.queryForAll().toIndexedSeq

    list.setAdapter(new MyAdapter(this, items, R.layout.list_child) {
      def bind(t: User, view: View) = {
        val (age, name) = view.tag[(TextView, TextView)]
        age.setText(t.age.toString)
        name.setText(t.name)
        view
      }

      def init(v: View) = {
        val List(age, name) = List(R.id.age, R.id.name).map{ v.gtTxt }
        v.setTag(age -> name)
      }
    })
  }
}

object EmptyText {
  def unapply(e: TextView) = e.getText.toString match {
    case null | "" => true
    case _         => false
  }
}

abstract class MyAdapter[T <: AnyRef](ctx: Context, items: IndexedSeq[T], layout: Int) extends BaseAdapter {
  override def getCount = items.size
  override def getItemId(p1: Int) = -1
  def init(v: View): Unit
  def bind(t: T, view: View): View
  override def getView(pos: Int, cv: View, parent: ViewGroup): View = (getItem(pos), cv) match {
    case (t, null) => bind(t, inflate(parent))
    case (t, v) => bind(t, v)
  }

  private def inflate(parent: ViewGroup) = {
    val v = LayoutInflater.from(ctx).inflate(layout, parent, false)
    init(v)
    v
  }

  override def getItem(p1: Int): T = items(p1)
}