export default {
  data: {
    //X軸のラベル情報
    labels: [
      "2023-04-01",
      "2023-04-02",
      "2023-04-03",
      "2023-04-04",
      "2023-04-05",
      "2023-04-06",
      "2023-04-07",
      "2023-04-08",
      "2023-04-09",
      "2023-04-10",
    ],
    //データセット
    datasets: [
      {
        label: "葡萄専心イチノセ",
        data: [40, 30, 50, 60, 70, 40, 10, 20, 25, 30],
        borderColor: "blue",
        backgroundColor: "#ffffff",
        fill: false,
        type: "line",
        lineTension: 0.3,
      },
      {
        label: "北社AKB1",
        data: [20, 10, 10, 20, 30, 40, 50, 30, 15, 10],
        borderColor: "red",
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
          value: new Date().toISOString(),
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
  },
};
