export default {
  data: {
    //X軸のラベル情報
    labels: [],

    //データセット
    datasets: [],
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
