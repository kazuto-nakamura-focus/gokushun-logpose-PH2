import moment from "moment";

export class LineGraphOptions {
    constructor() {
        this.subtitles = [];
        // * 仮の基本設定
        this.data = {
            chartOptions: {
                chart: {
                    type: 'line',
                    stacked: false,
                    height: 500,
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
                },
                dataLabels: {
                    enabled: false
                },
                colors: ['#2196f3', '#00b428', '#fb8c00'],
                markers: {
                    size: 0,
                },
                stroke: {
                    width: 2,
                    curve: 'straight',
                    //        dashArray: [0, 3]
                },
                title: {
                    text: undefined,
                    align: 'center',
                    style: {
                        fontSize: '14px',
                        fontWeight: 'bold',
                        fontFamily: "Helvetica, Arial, sans-serif",
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
                        fontFamily: "Helvetica, Arial, sans-serif",
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
    //* ============================================
    // オブジェクトリファレンス
    //* ============================================
    getChartOptions() { return this.data.chartOptions; }
    //* ============================================
    // グラフのタイトルを設定する
    //* ============================================
    setGraphTitle(title) {
        this.data.chartOptions.title.text = title;
    }
    //* ============================================
    // サブタイトルを追加する
    //* ============================================
    addSubTitleElement(name, value) {
        value = Math.round(value * 100) / 100;
        this.data.chartOptions.subtitle.text = name + " " + value;
    }
    //* ============================================
    // サブタイトルを作成する
    //* ============================================
    createSubtitle() {
        let text = "";
        for (const item of this.subtitles) {
            if (text.length > 0) text += " / ";
            text += item;
        }
        let yesterday = new moment().add(-1, 'day').format("MM / DD");
        this.data.chartOptions.subtitle.text = text + " " + yesterday;
    }
    //* ============================================
    // X軸のタイトルを作成する
    //* ============================================   
    setXScaleTitle(xtitle) { this.data.chartOptions.xaxis.title.text = xtitle; }
    //* ============================================
    // Y軸のタイトルを作成する
    //* ============================================ 
    setYScaleTitle(ytitle) { this.data.chartOptions.yaxis.title.text = ytitle; }
    //* ============================================
    // Y軸の逆タイトルを作成する
    //* ============================================ 
    setYScaleOppositeTitle(nameA, titleA, maxA, nameB, titleB, maxB) {
        let unitA = this.#setStep(maxA);
        let stepA = this.#setMax(maxA, unitA);
        let unitB = this.#setStep(maxB);
        let stepB = this.#setMax(maxB, unitB);

        this.data.chartOptions.yaxis = [{
            seriesName: nameA,
            title: {
                text: titleA,
            },
            stepSize: unitA,
            max: stepA,
            labels: {
                formatter: function (val) {
                    return Math.round(val * 100) / 100;
                },
            },

        }, {
            opposite: true,
            seriesName: nameB,
            title: {
                text: titleB,
            },
            stepSize: unitB,
            max: stepB,
            labels: {
                formatter: function (val) {
                    return Math.round(val * 100) / 100;
                },
            },
        }];
    }
    // ======================================================
    // X軸のカテゴリー設定
    // ======================================================
    setXCategory(category) { this.data.chartOptions.xaxis.categories = category; }
    //* ============================================
    // X軸アノテーションの設定 
    //* ============================================
    setXAnnotations(annotations) { this.data.chartOptions.annotations.xaxis = annotations; }
    //* ============================================
    // Y軸アノテーションの設定 
    //* ============================================
    setYAnnotations(annotations) { this.data.chartOptions.annotations.yaxis = annotations; }
    // ======================================================
    // Y軸の目盛りの値
    // ======================================================
    setYScale(max) {
        // this.data.chartOptions.yaxis.min = 0;
        let unit = this.#setStep(max);
        this.data.chartOptions.yaxis.stepSize = unit;
        this.data.chartOptions.yaxis.max = this.#setMax(max, unit);
    }
    // ======================================================
    // 色の指定の追加
    // ======================================================
    addColor(color) {
        this.data.chartOptions.colors.push(color);
    }
    // ======================================================
    // Y軸の目盛りの最大値設定する
    // ======================================================
    #setMax(max, unit) {
        let value = Math.trunc(max / unit) + 1;
        return unit * value + unit;
    }
    // ======================================================
    // Y軸の目盛りのステップサイズを設定する
    // ======================================================
    #setStep(max) {
        if (max >= 0) {
            // 桁数の算出
            let numberDigit = String(Math.floor(max)).length;
            // 2桁に変更する
            let tmp = max / Math.pow(10, (numberDigit - 2));
            // 目盛単位の設定
            let scale = 0;
            if ((tmp >= 10) && (tmp < 20)) scale = 1;
            else if ((tmp >= 20) && (tmp < 40)) scale = 2;
            else scale = 5;
            // 単位を戻して返却
            return scale * Math.pow(10, (numberDigit - 2));

        } else {
            // 数値を文字列に変換
            let numStr = max.toString();
            // 小数点の位置を見つける
            let decimalIndex = numStr.indexOf('.');
            // 小数点以下の部分を取得
            let decimalPart = numStr.slice(decimalIndex + 1);
            let numberDigit = 0;
            for (; numberDigit < decimalPart.length; numberDigit++) {
                if (decimalPart[numberDigit] !== '0') numberDigit++;
            }
            // 2桁に変更する
            let tmp = max * Math.pow(10, (numberDigit + 1));
            // 目盛単位の設定
            let scale = 0;
            if ((tmp >= 10) && (tmp < 20)) scale = 1;
            else if ((tmp >= 20) && (tmp < 40)) scale = 2;
            else scale = 5;
            // 単位を戻して返却
            return scale / Math.pow(10, (numberDigit - 1));
        }
    }
}