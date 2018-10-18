package milu.kiriu2010.gui.move

import android.graphics.Bitmap
import kotlin.math.PI
import kotlin.math.cos

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

    // 画像
    override lateinit var bmp: Bitmap
    // 回転角度(位置)
    override var al: AAngle = AAngle(180f,180f)
    // 回転角度(速度)
    override var av: AAngle = AAngle()
    // 回転による射影(位置)
    override var rl: AVector = AVector()
    // 回転による射影(大きさ)
    override var rs: AVector = AVector()

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

    // -------------------------------------------
    // 回転(物体の中心が軸)
    // -------------------------------------------
    override fun rotateByCenter() {
        // 回転(刻み)だけ回転させる
        al.x += av.x
        al.x = al.x%360
        al.y += av.y
        al.y = al.y%360
    }

    // -------------------------------------------
    // 回転による射影位置を更新
    // -------------------------------------------
    override fun updateByReflect() {
        // 回転により、物体の位置は変更せず、
        // 射影位置・大きさを変えることとする
        // 射影大きさは"正→裏/負→表"とする
        rs.x = -1f * wh.x * cos(al.y/180.0* PI).toFloat()
        rs.y = -1f * wh.y * cos(al.x/180.0* PI).toFloat()
        rl.x = (il.x+wh.x/2f) - rs.x/2f
        rl.y = (il.y+wh.y/2f) - rs.y/2f
    }


}
