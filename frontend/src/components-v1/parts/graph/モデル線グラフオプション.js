export class ModelLineGraphOptions {
    constructor() {
        this.subtitles = [];
        // * 仮の基本設定
        this.data = {
            parent: null,
            series: [{
                name: null,
                data: []
            }],
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
    getSeries() { return this.data.series; }
    getParent() { return this.data.parent; }
    //* ============================================
    // ローディングの親情報を設定する
    //* ============================================
    setLoadingParent(parent) {
        parent.isLoading = true;
        this.data.parent = parent;
    }
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
        let value = Math.round(source.estimated * 100) / 100;
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
        this.data.chartOptions.yaxis.min = 0;
        let unit = this.#setStep(max);
        this.data.chartOptions.yaxis.stepSize = unit;
        this.data.chartOptions.yaxis.max = this.#setMax(max, unit);
    }
    // ======================================================
    // Y軸の目盛りの最大値設定する
    // ======================================================
    #setMax(max, unit) {
        let value = Math.trunc(max / unit) + 1;
        return unit * value;
    }
    // ======================================================
    // Y軸の目盛りのステップサイズを設定する
    // ======================================================
    #setStep(max) {
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