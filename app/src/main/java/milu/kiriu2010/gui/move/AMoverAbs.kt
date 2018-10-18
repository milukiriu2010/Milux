package milu.kiriu2010.gui.move

import android.graphics.Bitmap

abstract class AMoverAbs{
    // 位置(中心)
    abstract var il: AVector
    // 速度
    abstract var iv: AVector
    // 加速度
    abstract var ia: AVector
    // 大きさ(物体)
    abstract var wh: AVector
    // 重さ
    abstract var mass: Float
    // 画像
    abstract var bmp: Bitmap
    // 回転角度(位置)
    abstract var al: AAngle
    // 回転角度(速度)
    abstract var av: AAngle
    // 回転による射影(位置)
    abstract var rl: AVector
    // 回転による射影(大きさ)
    abstract var rs: AVector

    abstract fun left(): Float
    abstract fun right(): Float
    abstract fun top(): Float
    abstract fun bottom(): Float
    // 横半径
    abstract fun radiusH(): Float
    // 縦半径
    abstract fun radiusV(): Float

    // -------------------------------------------
    // 回転(物体の中心が軸)
    // -------------------------------------------
    abstract fun rotateByCenter()
    // -------------------------------------------
    // 回転による射影位置を更新
    // -------------------------------------------
    abstract fun updateByReflect()
}
