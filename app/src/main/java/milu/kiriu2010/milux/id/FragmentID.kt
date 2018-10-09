package milu.kiriu2010.milux.id

enum class FragmentID(val id: String) {
    // メニューを表示するフラグメント
    // DrawerLayoutを使うと
    // 回転したときFragment上のcanvasが表示されないので利用してない
    ID_MENU("fragmentMenu"),
    // "設定"を表示するフラグメント
    ID_SETTINGS("fragmentSettings"),
    // "About"を表示するフラグメント
    ID_ABOUT("fragmentAbout")
}