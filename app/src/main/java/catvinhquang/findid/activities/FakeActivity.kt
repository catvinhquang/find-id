package catvinhquang.findid.activities

import android.app.Activity
import android.os.Bundle

class FakeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }

}
