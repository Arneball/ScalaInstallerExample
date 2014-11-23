package com.example.android2111.app

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.{ListView, TextView}
import com.example.android2111.app.Implicits._
import com.example.android2111.app.model.User
import com.example.android2111.app.util.MyCursorAdapter

class ListActivity extends ActivityExtras {
  lazy val ageField = this.gtTxt(R.id.age)
  lazy val nameField = this.gtTxt(R.id.name)
  lazy val button = this.fid(R.id.add_user)
  lazy val list = this.fid(R.id.listView).asInstanceOf[ListView]
  lazy val dao = DbAdapter.getDao[User]
  lazy val adapter = new MyCursorAdapter[User](this, newCursor, R.layout.list_child) {
    override def doBind(v: View, t: User): View = {
      v.gtTxt(R.id.age).setText(t.age.toString)
      v.gtTxt(R.id.name).setText(t.name)
      v
    }
  }

  private def newCursor = DbAdapter.dao2cursor[User]

  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    setContentView(R.layout.activity_list)

    button.setCl{
      List(ageField -> "Age", nameField -> "Name").collectFirst{
        case (EmptyText(), label) => toast(s"$label must not be empty")
      }.getOrElse{
        dao.create(new User(page = ageField.text.toInt, pname = nameField.text)) match {
          case 1 =>
            toast("Success")
            adapter.swapCursor(newCursor)
          case _ => toast("Failed to add user")
        }
      }
    } // end setCl
    list.setAdapter(adapter)
  }
}

object EmptyText {
  def unapply(e: TextView) = TextUtils.isEmpty(e.text)
}