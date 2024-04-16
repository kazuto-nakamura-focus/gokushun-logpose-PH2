//import { concat } from "core-js/core/array";
import moment from "moment";


const todayXaxisAnnotation = {
  x: new Date().getTime(),
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
        subtitle: {
          text: undefined,
          align: 'center',
          margin: 10,
          offsetX: 0,
          offsetY: 30,
          floating: false,
          style: {
            fontSize: '10pt',
            fontWeight: 'normal',
            fontFamily: undefined,
            color: '#FF6666'
          },
        },
        fill: {
          colors: ['#1A73E8', '#1A73E8']
        },
        yaxis: {
          labels: {
            formatter: function (val) {
              return Math.round(val * 100) / 100;
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
          yaxis: [],
          xaxis: []
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
    // * サブタイトル
    if (null != source.estimated) {
      let yesterday = new moment().add(-1, 'day').format("MM / DD");
      let value = Math.round(source.estimated * 100) / 100;
      this.data.chartOptions.subtitle.text = ytitle + " " + value + " ( " + yesterday + " )";
    }
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
          borderWidth: 1,
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

    this.data.chartOptions.annotations.xaxis.push(todayXaxisAnnotation);

    this.data.chartOptions.yaxis.min = 0;
    this.data.chartOptions.xaxis.categories = source.category;
    let unit = this.setStep(source.yend);
    this.data.chartOptions.yaxis.stepSize = unit;
    this.data.chartOptions.yaxis.max = this.setMax(source.yend, unit);
  }
  // ======================================================
  // Y軸の目盛りの最大値設定する
  // ======================================================
  setMax(max, unit) {
    let value = Math.trunc(max / unit) + 1;
    return unit * value;
  }
  // ======================================================
  // Y軸の目盛りのステップサイズを設定する
  // ======================================================
  setStep(max) {
    // * 計算を簡単にするため、桁をそろえる
    let munit = 10 * 15;
    if (max > munit) return 20;
    munit = 5 * 15;
    if (max > munit) return 10;
    munit = 1 * 15;
    if (max > munit) return 5;
    munit = 0.5 * 15;
    if (max > munit) return 1;
    munit = 0.1 * 15;
    if (max > munit) return 0.5;
    return 0.1;
  }
}
