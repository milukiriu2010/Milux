package milu.kiriu2010.gui.move

// ---------------------------------
// 物体データ(長方形)
// ---------------------------------
//   ・位置(中心) + 速度 + 加速度
//   ・大きさ(物体)
//   ・大きさ(可動範囲:左上)
//   ・大きさ(可動範囲:右下)
//   ・形の種類
//   ・重さ
// ---------------------------------
// 2018.10.16
// ---------------------------------
class AMoverRect(
        // 位置(中心)
        override var il: AVector = AVector(),
        // 速度
        override var iv: AVector = AVector(),
        // 加速度
        override var ia: AVector = AVector(),
        // 大きさ(物体)
        override var wh: AVector = AVector(),
        // 重さ
        override var mass: Float = 1f
): AMoverAbs() {

    // 左
    override fun left(): Float {
        return il.x
    }

    // 右
    override fun right(): Float {
        return il.x + wh.x
    }

    // 上
    override fun top(): Float {
        return il.y
    }

    // 下
    override fun bottom(): Float {
        return il.y + wh.y
    }

    // 横半径
    override fun radiusH(): Float {
        return wh.x/2f
    }

    // 縦半径
    override fun radiusV(): Float {
        return wh.y/2f
    }
}
