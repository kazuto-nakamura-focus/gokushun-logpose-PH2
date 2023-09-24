export class TopDataParser {
  constructor() { }
  // * サーバから取得したデータを解析する。
  // * データ構造はcom.logpose.ph2.api.dto.DataSummaryDTO参照
  parse(data) {
    var returnList = [];
    // * 表示ラベルとIDのMap
    var labeMap = {
      1: "温度(℃)",
      2: "湿度(%RH)",
      3: "日射(%RH)",
      4: "樹液流",
      5: "デンドロ(μm)",
      6: "葉面濡れ",
      7: "土壌水分",
      8: "土壌温度"
    };
  for(const dataElem of data) {
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
    for (var i = 1; i < 9; i++) {
      var koumoku = {};
      koumoku.name = labeMap[i];
      var hasData = false;
      for (const elem of dataElem.dataList) {
        if (elem.id == i) {
          var n = 2 ;	// 小数点第n位まで残す
          elem.value = Math.floor( elem.value * Math.pow( 10, n ) ) / Math.pow( 10, n ) ;
          koumoku.value = elem.value;
          koumoku.variable = elem.id;
          koumoku.date = elem.castedAt.substring(0,19);
          item.items.push(koumoku);
          hasData = true;
          break;
        }
      }
      if (!hasData) {
        koumoku.value = "---";
        koumoku.variable = "";
        koumoku.date = "";
        item.items.push(koumoku);
      }
    }
    returnList.push(item);
  }
    return returnList;
  }
}

