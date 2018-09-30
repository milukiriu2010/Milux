package milu.kiriu2010.util

class LimitedArrayList<E>(initialCapacity: Int,
                          // リストに格納可能な数
                          val limit: Int = 10)
    : ArrayList<E>(initialCapacity) {

    override fun add(index: Int, element: E) {
        val ret = super.add(index, element)

        // リミットを超えていたら最後の要素を削除
        while ( size > limit ) {
            super.remove(super.get(size-1))
        }

        return ret
    }

    override fun add(element: E): Boolean {
        val ret = super.add(element)

        // リミットを超えていたら最後の要素を削除
        while ( size > limit ) {
            super.remove(super.get(size-1))
        }

        return ret
    }
}