package milu.kiriu2010.gui.move

// --------------------------------
// 位置補正
// --------------------------------
// 端を完全に超えていたら、
// 反対側の端へ移動
// --------------------------------
data class ACorrectPosLR02(
        // 大きさ(可動範囲:左上)
        var rlt: AVector = AVector(),
        // 大きさ(可動範囲:右下)
        var rrb: AVector = AVector()
        ) {

    fun doo( mover: AMoverAbs ) {
        // ---------------------------------
        // 物体の左端が可動範囲の右端
        // を完全に超えていたら
        // 可動範囲の左端に物体の左端を移動
        // ---------------------------------
        if ( mover.left() >= rrb.x ) {
            mover.il.x = rlt.x
            //mover.il.x = mover.rl.x - (rrb.x-rlt.x)
        }
        // ---------------------------------
        // 物体の右端が可動範囲の左端
        // を完全に超えていたら
        // 可動範囲の右端に物体の右端を移動
        // ---------------------------------
        else if ( mover.right() <= rlt.x ) {
            mover.il.x = rrb.x - mover.wh.x
        }

        // ---------------------------------
        // 物体の上端が可動範囲の下端
        // を完全に超えていたら
        // 可動範囲の上端に物体の上端を移動
        // ---------------------------------
        if ( mover.top() >= rrb.y ) {
            mover.il.y = rlt.y
        }
        // ---------------------------------
        // 物体の下端が可動範囲の上端
        // を完全に超えていたら
        // 可動範囲の下端に物体の上端を移動
        // ---------------------------------
        else if ( mover.bottom() <= rlt.y ) {
            mover.il.y = rlt.y - mover.wh.y
        }
    }
}
