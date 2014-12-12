package com.example.android2111.app

import android.app.Activity
import android.os.Bundle
import Implicits._

class MainActivity extends Activity with ActivityExtras {
  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    setContentView(R.layout.activity_main)
    this.fid(R.id.button_list_activity).setCl{
      startActivity[ListActivity]
    }
    this.fid(R.id.button_actor_activity).setCl{
      startActivity[ActorActivity]
    }
    this.fid(R.id.button_fsm_activity).setCl {
      startActivity[FsmActorActivity]
    }
  }
}

