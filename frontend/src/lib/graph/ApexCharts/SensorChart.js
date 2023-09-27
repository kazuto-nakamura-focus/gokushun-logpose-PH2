//import { concat } from "core-js/core/array";
import moment from "moment";
export class SensorChart {
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
          toolbar: {
            autoSelected: 'zoom'
          },
          events: {
            parent : null,
            mounted: function() {
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
          width: [3, 3],
          curve: 'straight',
          dashArray: [0, 3]
        },
        title: {
          text: '',
          align: 'center',
          style: {
            fontSize: '14px',
            fontWeight: 'bold',
            fontFamily: undefined,
            color: '#263238'
          },
        },
        fill: {
          colors: ['#1A73E8']
        },
        yaxis: {
          labels: {
            formatter: function (val) {
              return Math.round(val);
            },
          },
          title: {
            text: ''
          },
        },
        xaxis: {
          categories: [],
          type: 'datetime',
          tickAmount: 12, // 365日の12分割
          title: {
            text: '',
            offsetY: 10,
          },
          labels: {
            formatter: function (val) {
              return moment(val).format("MM/DD");
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
        }
      }
    }
  }
  setLoadingParent(parent){
    parent.isLoading = true;
    this.data.parent = parent;
  }
  
  setOptions(title, ytitle, source) {
    //* タイトル
    this.data.chartOptions.title.text = title;
    //* Y軸タイトル
    this.data.chartOptions.yaxis.title.text = ytitle;
    //* アノテーション
    let annotations = []
    if ((source.annotations != null) && (source.annotations.length > 0)) {
      for (const item of source.annotations) {
        let annotation = {
          y: item.value,
          label: {
            borderColor: '#00E396',
            position: "left",
            textAnchor: "start",
            style: {
              color: '#00B428',
            },
            text: item.name
          }
        };
        annotations.push(annotation);
      }
      if (annotations.length > 0) {
        this.data.chartOptions.annotations.yaxis = annotations;
      }
    }
    this.data.chartOptions.yaxis.max = source.YEnd;
    this.data.chartOptions.yaxis.min = source.YStart;
    this.data.chartOptions.xaxis.categories = source.category;

  }
}
