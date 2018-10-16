package milu.kiriu2010.gui.move

// ---------------------------------
// 物体(形の種類)
// ---------------------------------
enum class ShapeType {
    // 四角
    RECT,
    // 円
    CIRCLE
}

// ---------------------------------
// 物体データ
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
data class AMover(
        // 位置(中心)
        var il: AVector = AVector(),
        // 速度
        var iv: AVector = AVector(),
        // 加速度
        var ia: AVector = AVector(),
        // 大きさ(物体)
        var wh: AVector = AVector(),
        // 大きさ(可動範囲:左上)
        var rlt: AVector = AVector(),
        // 大きさ(可動範囲:右下)
        var rrb: AVector = AVector(),
        // 形の種類
        var st: ShapeType = ShapeType.RECT,
        // 重さ
        var mass: Float = 1f
) {

    // 左
    fun left(): Float {
        return il.x - radiusH()
    }

    // 右
    fun right(): Float {
        return il.x + radiusH()
    }

    // 上
    fun top(): Float {
        return il.y - radiusV()
    }

    // 下
    fun bottom(): Float {
        return il.y + radiusV()
    }

    // 横半径
    fun radiusH(): Float {
        return wh.x/2f
    }

    // 縦半径
    fun radiusV(): Float {
        return wh.y/2f
    }

    // ------------------------------
    // 位置補正
    // ------------------------------
    // 端を完全に超えていたら、
    // 反対側の端へ移動
    // ------------------------------
    fun correctPos01() {
        // ---------------------------------
        // 物体の左端が可動範囲の右端
        // を完全に超えていたら
        // 可動範囲の左端に物体の右端を移動
        // ---------------------------------
        if ( left() >= rrb.x ) {
            il.x = rlt.x - radiusH()
        }
        // ---------------------------------
        // 物体の右端が可動範囲の左端
        // を完全に超えていたら
        // 可動範囲の右端に物体の左端を移動
        // ---------------------------------
        else if ( right() <= rlt.x ) {
            il.x = rrb.x + radiusH()
        }

        // ---------------------------------
        // 物体の上端が可動範囲の下端
        // を完全に超えていたら
        // 可動範囲の上端に物体の下端を移動
        // ---------------------------------
        if ( top() >= rrb.y ) {
            il.y = rlt.y - radiusV()
        }
        // ---------------------------------
        // 物体の下端が可動範囲の上端
        // を完全に超えていたら
        // 可動範囲の下端に物体の上端を移動
        // ---------------------------------
        else if ( bottom() <= rlt.y ) {
            il.y = rlt.y + radiusV()
        }
    }
}