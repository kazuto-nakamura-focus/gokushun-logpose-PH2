//import { concat } from "core-js/core/array";
import moment from "moment";


const todayXaxisAnnotation = {
  x: moment().format("YYYY/MM/DD"),
  borderColor: '#FF0000',
  strokeDashArray: 0,
  label: {
    borderColor: '#FF0000',
    show: true,
    text: `TODAY（${moment().format("YYYY/MM/DD")}）`,
    orientation: 'horizontal',
    style: {
      color: '#FF0000',
    },
  },
}

export class GrowthChart {
  constructor() {
    this.data = {
      parent: null,
      series: [{
        name: null,
        data: []
      }],
      chartOptions: {
        chart: {
          type: 'area',
          stacked: false,
          height: 350,
          animations: {
            enabled: false,
          },
          zoom: {
            type: 'x',
            enabled: true,
            autoScaleYaxis: false
          },
          toolbar: {
            autoSelected: 'zoom'
          },
          events: {
            parent: null,
            mounted: function () {
              this.data.parent.isLoading = false;
            }.bind(this)
          }
        },
        dataLabels: {
          enabled: false
        },
        markers: {
          size: 0,
        },
        stroke: {
          width: 2,
          curve: 'straight',
          //        dashArray: [0, 3]
        },
        title: {
          text: '生育推定',
          align: 'center',
          style: {
            fontSize: '14px',
            fontWeight: 'bold',
            fontFamily: undefined,
            color: '#263238'
          },
        },
        fill: {
          colors: ['#1A73E8', '#1A73E8']
        },
        yaxis: {
          labels: {
            formatter: function (val) {
              return Math.round(val);
            },
          },
          title: {
            text: '累積F値'
          },
        },
        xaxis: {
          categories: [],
          type: 'datetime',
          tickAmount: 12, // 365日の12分割
          axisTicks: { show: true, },
          title: {
            text: '',
            offsetY: -20,
          },
          labels: {
            formatter: function (val) {
              return moment(val).format("YYYY/MM/DD");
            },
          },
        },
        tooltip: {
          shared: false,
          y: {
            formatter: function (val) {
              return val;
            }
          }
        },
        annotations: {
          yaxis: []
        },
      }
    }
  }
  setLoadingParent(parent) {
    parent.isLoading = true;
    this.data.parent = parent;
  }
  setOptions(title, xtitle, ytitle, source, year, digits) {
    if (title == "生育ステージ推定モデル") {
      this.data.chartOptions.chart.height = 500;
    }
    if (digits != null) {
      this.data.chartOptions.yaxis.labels.formatter = (value) => {
        return Number.parseFloat(value).toFixed(digits);
      }
    }
    //* タイトル
    this.data.chartOptions.title.text = title;
    //* X軸タイトル
    this.data.chartOptions.xaxis.title.text = xtitle;
    //* Y軸タイトル
    this.data.chartOptions.yaxis.title.text = ytitle;
    //* Y軸の最高値
    let max = source.yend;
    //* アノテーション
    let annotations = []
    if ((source.annotations != null) && (source.annotations.length > 0)) {
      for (const item of source.annotations) {
        // * 最高値がアノテーションの値の方が高い場合
        if (max < item.value) {
          max = item.value;
        }
        var annotation = {
          y: item.value,
          borderColor: item.color,
          borderWidth: 2,
          strokeDashArray: 0,
          label: {
            borderColor: item.color,
            position: "left",
            textAnchor: "start",
            text: item.name,
            style: {
              color: '#000000',
            },
          }
        }
        annotations.push(annotation);
      }
      if (annotations.length > 0) {
        this.data.chartOptions.annotations.yaxis = annotations;
      } else {
        this.data.chartOptions.annotations.yaxis = [];
      }
    } else {
      this.data.chartOptions.annotations.yaxis = [];
    }
    //選択した年が表示日の年と一致している場合、Todayアノテーション線を表示する。
    if (year != null) {
      const today = moment();
      const todayYear = today.year();
      if (year == todayYear) {
        const strToday = today.format("YYYY/MM/DD");
        todayXaxisAnnotation["x"] = strToday;
        todayXaxisAnnotation["label"]["text"] = `TODAY（${strToday}）`;
        this.data.chartOptions.annotations.xaxis = [todayXaxisAnnotation];
      }
    }

    // this.data.chartOptions.yaxis.max = source.yend;
    this.data.chartOptions.yaxis.min = 0;
    this.data.chartOptions.xaxis.categories = source.category;
    this.data.chartOptions.yaxis.max = this.setMax(source.yend);
  }
  // ======================================================
  // Y軸のメモリを設定する
  // ======================================================
  setMax(max) {
    // * 分割値が10以上の場合は10で割って小数点を四捨五入した後で10倍する
    if (max > 10) {
      max = Math.ceil(max / 10) * 10;
    } else if (max < 0) {
      // * 分割値が0以下の場合は10をかけて小数点を除いた上で10で割る
      max = Math.ceil(max * 10) / 10;
    }
    return max;
  }
}
