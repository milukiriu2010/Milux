package milu.kiriu2010.milux.gui

interface SelectedListener {
    // ページ選択された→true
    // ページ選択されない→false
    fun onSelected( selected: Boolean )
}