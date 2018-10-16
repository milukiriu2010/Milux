package milu.kiriu2010.gui.move

import java.lang.Exception
import kotlin.math.sqrt

// -----------------------------------
// 物体データ
// -----------------------------------
//   位置 or 速度 or 加速度 or 大きさ
// -----------------------------------
// 2018.10.16
// -----------------------------------
data class AVector(
        var x: Float = 0f,
        var y: Float = 0f) {

    constructor( av: AVector ): this(av.x, av.y) {
    }

    // 移動
    fun set( av: AVector ): AVector {
        x = av.x
        y = av.y
        return this
    }

    // 移動(加算)
    fun add( dv: AVector ): AVector {
        x += dv.x
        y -= dv.y
        return this
    }

    // 移動(減算)
    fun sub( dv: AVector ): AVector {
        x -= dv.x
        y -= dv.y
        return this
    }

    // 移動(乗算)
    fun mult( d: Float ): AVector {
        x *= d
        y *= d
        return this
    }

    // 移動(除算)
    fun div( d: Float ): AVector {
        if ( d == 0f ){
            throw Exception("unable to divide by 0")
        }
        else {
            x /= d
            y /= d
        }
        return this
    }

    // ベクトルの大きさ
    fun mag(): Float {
        return sqrt(x*x+y*y)
    }

    // 単位ベクトルに変換
    fun normalize(): AVector {
        val mag = mag()
        if ( mag != 0f ) {
            x /= mag
            y /= mag
        }
        return this
    }

    // "ベクトルの大きさ"の最大リミットを指定
    fun limitMax(limit: Float): AVector {
        if ( limit < 0f ) {
            throw Exception("limit should be > 0.")
        }
        val mag = mag()

        if ( mag == 0f ) {
        }
        else if ( mag > limit ) {
            x = x*limit/mag
            y = y*limit/mag
        }
        return this
    }

    // "ベクトルの大きさ"の最小リミットを指定
    fun limitMin(limit: Float): AVector {
        if ( limit < 0f ) {
            throw Exception("limit should be > 0.")
        }
        val mag = mag()
        if ( mag == 0f ) {
        }
        else if ( mag < limit ) {
            x = x*limit/mag
            y = y*limit/mag
        }
        return this
    }

    // "ベクトルの大きさ"の範囲を指定
    fun contain( magMin: Float, magMax: Float ): AVector {
        if ( magMin > magMax ) {
            throw Exception("magMax should be grater than magMin.")
        }
        limitMax(magMax)
        limitMin(magMin)
        return this
    }
}
