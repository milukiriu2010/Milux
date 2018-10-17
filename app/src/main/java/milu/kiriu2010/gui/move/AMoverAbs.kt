package milu.kiriu2010.gui.move

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

    abstract fun left(): Float
    abstract fun right(): Float
    abstract fun top(): Float
    abstract fun bottom(): Float
    // 横半径
    abstract fun radiusH(): Float
    // 縦半径
    abstract fun radiusV(): Float
}
