package catvinhquang.findid.services

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import catvinhquang.findid.activities.FakeActivity

/**
 * Created by QuangCV on 01-May-2019
 **/

class MyTileService : TileService() {

    companion object {
        var isActive = false
    }

    override fun onTileAdded() {
        updateState(Tile.STATE_INACTIVE)
    }

    override fun onClick() {
        updateState(if (qsTile.state == Tile.STATE_INACTIVE) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE)
        startActivityAndCollapse(Intent(this, FakeActivity::class.java))
    }

    private fun updateState(newState: Int) {
        isActive = newState == Tile.STATE_ACTIVE
        MyAccessibilityService.setActive(isActive)
        qsTile.state = newState
        qsTile.updateTile()
    }

}