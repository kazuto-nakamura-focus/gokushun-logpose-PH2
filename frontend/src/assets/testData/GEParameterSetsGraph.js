export default {
  data: {
    //X軸のラベル情報
    labels: [
      "2023-01",
      "2023-02",
      "2023-03",
      "2023-04",
      "2023-05",
      "2023-06",
      "2023-07",
      "2023-08",
      "2023-09",
      "2023-10",
      "2023-11",
      "2023-12"
    ],
    //データセット
    datasets: [
      {
        label: "変更前",
        data: [1, 2, 3, 2, 0, -1, -3, -2, -1, 0, 7, 6],
        borderColor: "#C80046",
        backgroundColor: "#ffffff",
        fill: false,
        type: "line",
        lineTension: 0.3,
      },
      {
        label: "変更後",
        data: [2, 3, 4, 2, 0, -2, -4, -1, -1.6, 0.8, 7.9, 2.7],
        borderColor: "#5A00A0",
        backgroundColor: "#ffffff",
        fill: false,
        type: "line",
        lineTension: 0.3,
      },
    ],
  },
  //グラフのオプション
  options: {
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
            labelString: "年度", // ラベル
            fontColor: "blue", // 文字の色
            fontFamily: "sans-serif",
            fontSize: 16, // フォントサイズ
          },
          ticks: {
            callback: function(label){
              return label.split(":")[0]; //":"で分割した1つ目を"開催年"として抽出
            }
          }
         
        },
      ],
      yAxes: [
        {
          ticks: {
            min: -10,
            max: 10,
            stepSize: 1
          },
          // ticks: {
          //   beginAtZero: true,
          //   stepSize: 1,
          //   max: 10,
          // },
          scaleLabel: {
            // 軸ラベル
            display: true, // 表示の有無
            labelString: "パラメータセット", // ラベル
            fontColor: "blue", // 文字の色
            fontFamily: "sans-serif",
            fontSize: 16, // フォントサイズ
          },
        },
      ],
    },
    annotation: {
      // annotations: [
      //   {
      //     drawTime: "beforeDatasetsDraw", // overrides annotation.drawTime if set
      //     id: "a-line-1", // optional
      //     type: "line",
      //     mode: "vertical",
      //     scaleID: "x-axis-0",
      //     value: new Date().toISOString(),
      //     borderColor: "red",
      //     borderWidth: 2,

      //     // Fires when the user clicks this annotation on the chart
      //     // (be sure to enable the event in the events array below).
      //     onClick: function (e) {
      //       // `this` is bound to the annotation element
      //       console.log(e);
      //     },
      //   },
      // ],
    },
  },
};
