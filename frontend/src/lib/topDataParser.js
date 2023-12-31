export class TopDataParser {
  constructor() { }
  // * サーバから取得したデータを解析する。
  // * データ構造はcom.logpose.ph2.api.dto.DataSummaryDTO参照
  parse(data) {
    var returnList = [];
    for (const dataElem of data) {
      // * 各データ要素を表示用アイテムに代入する
      var item = {};
      item.title = dataElem.field;
      item.comment = dataElem.device;
      item.date = dataElem.date;
      item.fieldId = dataElem.fieldId;
      item.deviceId = dataElem.deviceId;
      item.state = new Object;
      item.state = false;
      item.items = [];

      for (const elem of dataElem.dataList) {
        var koumoku = {};
        koumoku.name = elem.name;
        if (elem.value != null) {
          var n = 2;	// 小数点第n位まで残す
          elem.value = Math.floor(elem.value * Math.pow(10, n)) / Math.pow(10, n);
          koumoku.value = elem.value;
          koumoku.variable = elem.id;
          koumoku.date = elem.castedAt.substring(0, 19);
          item.items.push(koumoku);
        } else {
          koumoku.value = "---";
          koumoku.variable = "";
          koumoku.date = "";
          item.items.push(koumoku);
        }
      }
      for (let i = item.items.length; i < 8; i++) {
        koumoku.name = "---"
        koumoku.value = "---";
        koumoku.variable = "";
        koumoku.date = "";
        item.items.push(koumoku);
      }

      returnList.push(item);
    }
    return returnList;
  }
}

