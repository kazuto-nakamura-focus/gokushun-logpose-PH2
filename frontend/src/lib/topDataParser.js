import moment from "moment";

export class TopDataParser {
  constructor() { }
  // * サーバから取得したデータを解析する。
  // * データ構造はcom.logpose.ph2.api.dto.DataSummaryDTO参照
  parse(data) {
    var returnList = [];
    for (const dataElem of data) {
      // * 各データ要素を表示用アイテムに代入する
      var item = {};
      item.title = dataElem.field; // 圃場名
      item.comment = dataElem.device;
      item.date = dataElem.date; // 更新時刻
      if (null != item.date) {
        item.date = item.date.substring(0, 19);
      }

      item.fieldId = dataElem.fieldId;
      item.deviceId = dataElem.deviceId;
      item.forecast = [];
      item.wheather_text = "";
      item.wheather_url = "";
      for (var i = 0; i < dataElem.forecastList.length; i++) {
        if (i == 0) {
          item.wheather_url = dataElem.forecastList[0].url;
          item.wheather_text = dataElem.forecastList[0].text;
        } else {
          var wheather = new Object;
          wheather.url = dataElem.forecastList[i].url;
          wheather.text = dataElem.forecastList[i].text;
          var date = moment(dataElem.forecastList[i].date);
          wheather.time = date.format("Ah時");
          item.forecast.push(wheather);
        }
      }
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
          koumoku.unit = elem.unit;
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

