export class RawDataGraphPanel {
    constructor() {
        this.id;
        this.title;
        this.subname;
        this.chartOption;
        this.chartData;
    }
    //* ============================================
    // パネルタイトルの設定
    //* ============================================
    setPanelTitle(titles) {
        this.title = titles.main;
        this.subname = titles.sub;
    }
    //* ============================================
    // グラフデータの作成
    //* ============================================
    setGraphData(settings) {
        this.chartOption = settings.getChartOptions();
        this.chartData = settings.getSeries();
    }
}