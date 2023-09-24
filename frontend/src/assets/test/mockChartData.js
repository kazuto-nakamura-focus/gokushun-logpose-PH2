export default {
  data: {
    //X軸のラベル情報
    labels: [
      "2022-12-01",
      "2023-01-01",
      "2023-02-01",
      "2023-03-01",
      "2023-04-01",
      "2023-05-01",
      "2023-06-01",
      "2023-07-01",
      "2023-08-01",
      "2023-09-01",
      "2023-10-01",
      "2023-11-01",
      "2023-12-01",
      "2024-01-01",
    ],
    //データセット
    datasets: [
      {
        label: "実績",
        data: [NaN, 30, 50, 60, 70],
        borderColor: "#C80046",
        backgroundColor: "#ffffff",
        fill: false,
        type: "line",
        lineTension: 0.3,
      },
      {
        label: "推定",
        data: [NaN, NaN, NaN, NaN, 70, 77, 80, 85],
        borderDash: [5, 5],
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
  },
};
