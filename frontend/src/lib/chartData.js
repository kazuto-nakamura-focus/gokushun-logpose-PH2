export class ChartDataHelper {
  constructor() {
    this.data = {
      //X軸のラベル情報
      labels: [],
      //データセット
      datasets: [
        {
          label: "実績",
          data: [],
          borderColor: "#C80046",
          backgroundColor: "#ffffff",
          fill: false,
          type: "line",
          lineTension: 0.3,
        },
        {
          label: "推定",
          data: [],
          borderDash: [5, 5],
          borderColor: "#5A00A0",
          backgroundColor: "#ffffff",
          fill: false,
          type: "line",
          lineTension: 0.3,
        },
      ],
    };
    //グラフのオプション
    this.options = {
      scales: {
        xAxes: [
          {
            type: "time",
            position: "bottom",
            time: {
              displayFormats: { day: "YYYY/MM" },
              tooltipFormat: "DD/MM/YY",
              unit: "month",
            },

            scaleLabel: {
              // 軸ラベル
              display: true, // 表示の有無
              labelString: "横軸ラベル（単位）", // ラベル
              fontColor: "blue", // 文字の色
              fontFamily: "sans-serif",
              fontSize: 16, // フォントサイズ
            },
          },
        ],
        yAxes: [
          {
            ticks: {
              beginAtZero: true,
              stepSize: 10,
              max: 110,
            },
            scaleLabel: {
              // 軸ラベル
              display: true, // 表示の有無
              labelString: "縦軸ラベル（単位）", // ラベル
              fontColor: "blue", // 文字の色
              fontFamily: "sans-serif",
              fontSize: 16, // フォントサイズ
            },
          },
        ],
      },
      annotation: {
        annotations: [
          {
            drawTime: "beforeDatasetsDraw", // overrides annotation.drawTime if set
            id: "a-line-1", // optional
            type: "line",
            mode: "vertical",
            scaleID: "x-axis-0",
            value: new Date().toLocaleDateString(),
            borderColor: "red",
            borderWidth: 2,

            // Fires when the user clicks this annotation on the chart
            // (be sure to enable the event in the events array below).
            onClick: function (e) {
              // `this` is bound to the annotation element
              console.log(e);
            },
          },
        ],
      },
    };
  }
  //* 選択されたデータ
  //private SelectedTarget selected;
  //* Y軸最小値 Double YStart;
  //* Y軸最大値 Double YEnd;
  //* X軸最小値(起点日) String XStart;
  //* X軸最大値(終点日) String XEnd;
  //* 値のリスト List<Double> values = new ArrayList<>();
  parseData(data) {
    var date = new Date(data.xstart);
    for (var i = 0; i < 366; i++) {
      var sdate = date.toLocaleString().substring(5, 9);
      this.data.labels.push(sdate)
      date.setDate(date.getDate() + 1);
    }
    this.data.datasets[0].data = data.values;
    this.data.datasets[1].data = data.predictValues;
  }
}
